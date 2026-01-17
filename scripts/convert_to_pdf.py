import os
import sys
import subprocess
import tempfile
import argparse

# GitHub-like CSS for PDF
# Includes basic Korean font support
GITHUB_CSS = """
body {
    font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Helvetica, Arial, sans-serif, "Apple Color Emoji", "Segoe UI Emoji", "Segoe UI Symbol";
    font-size: 16px;
    line-height: 1.5;
    word-wrap: break-word;
    color: #24292e;
    background-color: #fff;
    margin: 0;
    padding: 2rem;
}

h1, h2, h3, h4, h5, h6 {
    margin-top: 24px;
    margin-bottom: 16px;
    font-weight: 600;
    line-height: 1.25;
}

h1 { font-size: 2em; border-bottom: 1px solid #eaecef; padding-bottom: 0.3em; }
h2 { font-size: 1.5em; border-bottom: 1px solid #eaecef; padding-bottom: 0.3em; }
h3 { font-size: 1.25em; }
h4 { font-size: 1em; }
h5 { font-size: 0.875em; }
h6 { font-size: 0.85em; color: #6a737d; }

p { margin-top: 0; margin-bottom: 16px; }

code {
    padding: 0.2em 0.4em;
    margin: 0;
    font-size: 85%;
    background-color: #f6f8fa;
    border-radius: 6px;
    font-family: SFMono-Regular, Consolas, "Liberation Mono", Menlo, monospace;
}

pre {
    padding: 16px;
    overflow: auto;
    font-size: 85%;
    line-height: 1.45;
    background-color: #f6f8fa;
    border-radius: 6px;
}
pre code {
    background-color: transparent;
    padding: 0;
}

blockquote {
    padding: 0 1em;
    color: #6a737d;
    border-left: 0.25em solid #dfe2e5;
    margin: 0;
}

ul, ol { padding-left: 2em; margin-top: 0; margin-bottom: 16px; }

img { max-width: 100%; box-sizing: content-box; background-color: #fff; }

a { color: #0366d6; text-decoration: none; }
a:hover { text-decoration: underline; }

table {
    display: block;
    width: 100%;
    overflow: auto;
    border-spacing: 0;
    border-collapse: collapse;
    margin-bottom: 16px;
}
table tr {
    background-color: #fff;
    border-top: 1px solid #c6cbd1;
}
table tr:nth-child(2n) {
    background-color: #f6f8fa;
}
table th, table td {
    padding: 6px 13px;
    border: 1px solid #dfe2e5;
}
table th {
    font-weight: 600;
}
"""

def convert_to_pdf(input_file):
    if not os.path.isfile(input_file):
        print(f"Error: File '{input_file}' not found.")
        sys.exit(1)

    # Create temp CSS file
    with tempfile.NamedTemporaryFile(mode='w', suffix='.css', delete=False) as temp_css:
        temp_css.write(GITHUB_CSS)
        css_path = temp_css.name

    try:
        print(f"Converting '{input_file}' to PDF...")
        # npx md-to-pdf --stylesheet <css_path> <input_file>
        # Note: md-to-pdf saves the pdf in the same directory as input file by default
        subprocess.run(
            ['npx', '-y', 'md-to-pdf', '--stylesheet', css_path, input_file],
            check=True
        )
        print(f"Successfully converted '{input_file}'")
    except subprocess.CalledProcessError as e:
        print(f"Error during conversion: {e}")
        sys.exit(1)
    finally:
        # Cleanup temp CSS
        if os.path.exists(css_path):
            os.remove(css_path)

if __name__ == "__main__":
    parser = argparse.ArgumentParser(description="Convert Markdown to PDF using npx md-to-pdf")
    parser.add_argument("file", help="Path to the markdown file to convert")
    args = parser.parse_args()
    
    convert_to_pdf(args.file)
