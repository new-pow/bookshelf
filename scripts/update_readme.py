import os
import urllib.parse
import subprocess
from datetime import datetime

# Settings
ROOT_DIR = "."
README_FILE = "README.md"
MAX_DEPTH = 3
EXCLUDE_DIRS = {".git", ".github", ".idea", ".vscode", "scripts", ".obsidian", ".trash", "assets"}
EXCLUDE_FILES = {"README.md", "LICENSE"}

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


def main():
    til_files = []
    
    # 1. Walk directories
    for root, dirs, files in os.walk(ROOT_DIR):
        # Filter directories
        dirs[:] = [d for d in dirs if d not in EXCLUDE_DIRS and not d.startswith('.')]
        
        # Calculate depth
        rel_path = os.path.relpath(root, ROOT_DIR)
        if rel_path == ".":
            depth = 0
        else:
            depth = len(rel_path.split(os.sep))
        
        # If we are AT max depth, we can't go deeper, so clear dirs
        if depth >= MAX_DEPTH:
            dirs[:] = [] 
            
        for file in files:
            if file.endswith(".md") and file not in EXCLUDE_FILES:
                filepath = os.path.join(root, file)
                # Get category from root folder name (e.g., "☁️-Kubernetes")
                if rel_path == ".":
                    category = "Uncategorized"
                else:
                    category = rel_path.split(os.sep)[0]
                
                date = get_git_date(filepath)
                
                # Title parsing (naive: filename without extension)
                title = os.path.splitext(file)[0].replace("-", " ")
                
                # Try to read real title from file content (# Title)
                try:
                    with open(filepath, 'r', encoding='utf-8') as f:
                        lines = f.readlines()
                        for line in lines:
                            line = line.strip()
                            if line.startswith("# "):
                                title = line[2:].strip()
                                break
                except Exception:
                    pass

                til_files.append({
                    "path": filepath,
                    "rel_path": os.path.relpath(filepath, ROOT_DIR),
                    "title": title,
                    "category": category,
                    "depth": depth,
                    "date": date,
                    "filename": file
                })

    # 2. Sort by date for "Recent"
    til_files.sort(key=lambda x: x['date'], reverse=True)
    recent_tils = til_files[:3]
    
    # 3. Sort by category and path for "All"
    # Group by category
    files_by_category = {}
    for item in til_files:
        cat = item['category']
        if cat not in files_by_category:
            files_by_category[cat] = []
        files_by_category[cat].append(item)
        
    sorted_categories = sorted(files_by_category.keys())
    
    # 4. Generate Content
    content = []
    content.append(HEADER)
    
    # Total count
    # Note: original had _35 TILs and counting..._
    content.append(f"_{len(til_files)} TILs and counting..._\n")
    content.append("---\n")
    
    # Recent
    content.append("### 3 most recent TILs\n")
    for item in recent_tils:
        date_str = item['date'].strftime('%d %b %y %H:%M %z')
        # Encode URL
        url = urllib.parse.quote(item['rel_path'], safe='/')
        content.append(f"- [{item['title']}]({url}) - {date_str}")
    content.append("\n")
    
    # Categories Index
    content.append("### Categories\n")
    for cat in sorted_categories:
        if cat == "Uncategorized": continue
        # Encode anchor
        anchor = cat.lower().replace(" ", "-")
        # Handle special chars in anchor if needed (GitHub style)
        # Assuming simple for now
        content.append(f"- [{cat}](#{anchor})")
    content.append("\n")
    
    # Lists
    for cat in sorted_categories:
        if cat == "Uncategorized": continue
        
        anchor = cat.lower().replace(" ", "-")
        content.append(f"### [{cat}](#{anchor})\n")
        
        # Sort files in category by filename/title
        cat_files = sorted(files_by_category[cat], key=lambda x: x['title'])
        
        for item in cat_files:
            # Determine indentation based on depth relative to category root
            parts = item['rel_path'].split(os.sep)
            # parts: [Category, Sub, File] -> len 3 -> indent 1
            # parts: [Category, File] -> len 2 -> indent 0
            
            indent_level = len(parts) - 2 
            if indent_level < 0: indent_level = 0
            
            indent = "  " * indent_level
            url = urllib.parse.quote(item['rel_path'], safe='/')
            content.append(f"{indent}- [{item['title']}]({url})")
        content.append("\n")

    content.append(FOOTER)
    
    with open(README_FILE, 'w', encoding='utf-8') as f:
        f.write("\n".join(content))
    print(f"Successfully generated {README_FILE}")

if __name__ == "__main__":
    main()
