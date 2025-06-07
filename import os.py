import os
from pathlib import Path

def concatenate_java_files(base_project_dir, output_file_name):
    """
    Concatenates Java files from specified directories and a specific file
    into a single output file.

    Args:
        base_project_dir (str): The root directory of the recipe_db project.
        output_file_name (str): The name of the file to save concatenated content.
    """
    base_src_dir = Path(base_project_dir) / "src" / "main" / "java" / "recipedb"
    output_file_path = Path(base_project_dir) / output_file_name

    dirs_to_scan = ["dao", "model", "ui"]
    specific_files = [base_src_dir / "RecipeApp.java"]

    all_content = []

    # Process specific files first
    for file_path in specific_files:
        if file_path.exists() and file_path.is_file():
            all_content.append(f"# File: {file_path.relative_to(Path(base_project_dir))}\n")
            with open(file_path, 'r', encoding='utf-8') as f:
                all_content.append(f.read())
            all_content.append("\n\n" + "="*80 + "\n\n") # Separator
        else:
            print(f"Warning: Specific file not found - {file_path}")

    # Process directories
    for dir_name in dirs_to_scan:
        current_dir = base_src_dir / dir_name
        if current_dir.exists() and current_dir.is_dir():
            for java_file in sorted(current_dir.glob("*.java")):
                all_content.append(f"# File: {java_file.relative_to(Path(base_project_dir))}\n")
                with open(java_file, 'r', encoding='utf-8') as f:
                    all_content.append(f.read())
                all_content.append("\n\n" + "="*80 + "\n\n") # Separator
        else:
            print(f"Warning: Directory not found - {current_dir}")

    # Write to output file
    with open(output_file_path, 'w', encoding='utf-8') as outfile:
        outfile.write("".join(all_content))

    print(f"All specified Java files have been concatenated into: {output_file_path}")

if __name__ == "__main__":
    # Assuming the script is run from c:\Users\adria\Downloads\recipe_db\
    # or you provide the correct absolute path.
    project_directory = Path(__file__).parent.resolve() # Gets the directory where the script is located
    output_filename = "concatenated_java_sources.txt"
    concatenate_java_files(project_directory, output_filename)