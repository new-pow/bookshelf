import os
import urllib.parse
import subprocess
from datetime import datetime

# Settings
ROOT_DIR = "."
README_FILE = "README.md"
MAX_DEPTH = 3
EXCLUDE_DIRS = {".git", ".github", ".idea", ".vscode", "scripts", ".obsidian", ".trash", "assets"}
EXCLUDE_FILES = {"README.md", "README-old.md", "LICENSE", ".DS_Store"}

HEADER = """# TIL
> Today I Learned

A compilation of my daily learnings and insights while working and researching.
If you're interested in more in-depth discussions, check out [my blog][1].
Feel free to explore the most recent writeups below this README.

"""

FOOTER = """
[1]: https://new-pow.tistory.com
"""

def get_git_date(filepath):
    try:
        # Get the last commit date in ISO 8601 format
        result = subprocess.run(
            ['git', 'log', '-1', '--format=%cd', '--date=iso', filepath],
            capture_output=True, text=True, check=True
        )
        date_str = result.stdout.strip()
        if date_str:
            return datetime.strptime(date_str, '%Y-%m-%d %H:%M:%S %z')
    except Exception:
        pass
    # Fallback to file modification time if git fails or no commit
    return datetime.fromtimestamp(os.path.getmtime(filepath)).astimezone()

def get_title(filepath):
    filename = os.path.basename(filepath)
    title = os.path.splitext(filename)[0].replace("-", " ")
    try:
        with open(filepath, 'r', encoding='utf-8') as f:
            for line in f:
                line = line.strip()
                if line.startswith("# "):
                    return line[2:].strip()
    except Exception:
        pass
    return title

def build_tree(root_dir, max_depth):
    # Node structure: {'type': 'dir', 'name': 'foo', 'children': {}, 'files': [], 'depth': 0}
    root_node = {'type': 'dir', 'name': '.', 'children': {}, 'files': [], 'depth': 0}
    
    all_files_flat = []

    for root, dirs, files in os.walk(root_dir):
        # Filter directories in-place to prevent walking into excluded ones
        dirs[:] = [d for d in dirs if d not in EXCLUDE_DIRS and not d.startswith('.')]
        
        rel_path = os.path.relpath(root, root_dir)
        
        if rel_path == ".":
            current_depth = 0
            current_node = root_node
        else:
            parts = rel_path.split(os.sep)
            current_depth = len(parts)
            
            # Prune if we reached max depth
            if current_depth >= max_depth:
                dirs[:] = []
            
            # Navigate/Create nodes in tree
            current_node = root_node
            # We traverse from root down to current
            for idx, part in enumerate(parts):
                if part not in current_node['children']:
                    current_node['children'][part] = {
                        'type': 'dir', 
                        'name': part, 
                        'children': {}, 
                        'files': [], 
                        'depth': idx + 1
                    }
                current_node = current_node['children'][part]
        
        # Process files in current directory
        for file in files:
            if file.endswith(".md") and file not in EXCLUDE_FILES and not file.startswith('.'):
                filepath = os.path.join(root, file)
                title = get_title(filepath)
                date = get_git_date(filepath)
                
                file_item = {
                    'type': 'file',
                    'name': file,
                    'title': title,
                    'path': filepath,
                    'rel_path': os.path.relpath(filepath, root_dir),
                    'date': date
                }
                current_node['files'].append(file_item)
                all_files_flat.append(file_item)
        
        # Sort files in current node by title
        current_node['files'].sort(key=lambda x: x['title'].lower())

    return root_node, all_files_flat

def generate_markdown_tree_recursive(node, content_lines):
    # 1. Output Files
    for file_item in node['files']:
        url = urllib.parse.quote(file_item['rel_path'], safe='/')
        content_lines.append(f"- [{file_item['title']}]({url})")
    
    # 2. Output Directories (sorted)
    sorted_subdirs = sorted(node['children'].keys(), key=lambda s: s.lower())
    
    for dirname in sorted_subdirs:
        subnode = node['children'][dirname]
        
        if not subnode['files'] and not subnode['children']:
            continue
        
        content_lines.append(f"<details>")
        content_lines.append(f"<summary><strong>{dirname}</strong></summary>")
        content_lines.append("") # Blank line required for markdown parsing inside details
        
        generate_markdown_tree_recursive(subnode, content_lines)
        
        content_lines.append("")
        content_lines.append(f"</details>")

def main():
    print("Scanning directories...")
    root_node, all_files = build_tree(ROOT_DIR, MAX_DEPTH)
    
    # Sort by date for "Recent" section
    all_files.sort(key=lambda x: x['date'], reverse=True)
    recent_tils = all_files[:3]
    
    content = []
    content.append(HEADER)
    content.append(f"_{len(all_files)} TILs and counting..._\n")
    content.append("---\n")
    
    # Recent Section
    content.append("### 3 most recent TILs\n")
    for item in recent_tils:
        date_str = item['date'].strftime('%Y-%m-%d')
        url = urllib.parse.quote(item['rel_path'], safe='/')
        content.append(f"- [{item['title']}]({url}) - {date_str}")
    content.append("\n")
    
    content.append("### Categories\n")
    
    # Generate Tree Content
    # We treat root node slightly differently: we don't wrap root in a details tag.
    
    # 1. Files in Root
    for file_item in root_node['files']:
        url = urllib.parse.quote(file_item['rel_path'], safe='/')
        content.append(f"- [{file_item['title']}]({url})")
        
    # 2. Subdirectories in Root
    sorted_subdirs = sorted(root_node['children'].keys(), key=lambda s: s.lower())
    for dirname in sorted_subdirs:
        subnode = root_node['children'][dirname]
        
        if not subnode['files'] and not subnode['children']:
            continue
            
        content.append("")
        content.append(f"#### {dirname}")
        
        generate_markdown_tree_recursive(subnode, content)
        
        content.append("")
    
    content.append(FOOTER)
    
    with open(README_FILE, 'w', encoding='utf-8') as f:
        f.write("\n".join(content))
    print(f"Successfully generated {README_FILE}")

if __name__ == "__main__":
    main()
