import json
from wordfreq import zipf_frequency

INPUT_FILE = "single_wordnet_dictionary_expanded.json"
OUTPUT_FILE = "final_wordnet_dictionary.json"

MIN_ZIPF = 4.2  # Adjust as needed

def filter_dictionary():
    with open(INPUT_FILE, "r", encoding="utf-8") as f:
        dictionary = json.load(f)

    filtered_dict = {}

    for word, definitions in dictionary.items():
        score = zipf_frequency(word, "en")

        if score >= MIN_ZIPF:
            filtered_dict[word] = definitions

    print(f"Original words: {len(dictionary)}")
    print(f"Filtered words: {len(filtered_dict)}")

    with open(OUTPUT_FILE, "w", encoding="utf-8") as f:
        json.dump(filtered_dict, f, ensure_ascii=False, indent=2, sort_keys=True)


if __name__ == "__main__":
    filter_dictionary()