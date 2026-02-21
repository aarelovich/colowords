import json
import os
from collections import defaultdict

# -------------------------------
# CONFIGURATION
# -------------------------------

MIN_LEN = 3
MAX_LEN = 7
OUTPUT_FILE = "single_wordnet_dictionary.json"

# -------------------------------
# STEP 1: Build synset -> gloss map
# -------------------------------

def build_synset_map(directory):
    synset_to_defs = {}

    for filename in os.listdir(directory):
        if filename.startswith(("noun.", "verb.", "adj.", "adv.")) and filename.endswith(".json"):
            print(f"Loading synsets from {filename}")

            with open(os.path.join(directory, filename), "r", encoding="utf-8") as f:
                data = json.load(f)

                for synset_id, synset_data in data.items():

                    # Ensure this is a proper synset entry
                    if not isinstance(synset_data, dict):
                        continue

                    definitions = synset_data.get("definition", [])

                    if definitions:
                        # Store the list of definition strings
                        synset_to_defs[synset_id] = definitions

    print(f"Total synsets loaded: {len(synset_to_defs)}")
    return synset_to_defs


# -------------------------------
# STEP 2: Build word -> definitions map
# -------------------------------

def valid_word(word):
    return (
        MIN_LEN <= len(word) <= MAX_LEN
        and word.isalpha()
        and word.islower()
    )

def build_dictionary(directory, synset_map):
    word_to_definitions = defaultdict(set)

    for filename in os.listdir(directory):
        if filename.startswith("entries-") and filename.endswith(".json"):
            print(f"Processing entries from {filename}")
            with open(os.path.join(directory, filename), "r", encoding="utf-8") as f:
                data = json.load(f)

                for word, word_data in data.items():

                    if not valid_word(word):
                        continue

                    for pos_data in word_data.values():
                        senses = pos_data.get("sense", [])
                        for sense in senses:
                            synset_id = sense.get("synset")
                            if synset_id and synset_id in synset_map:
                               definitions = synset_map[synset_id]
                               for definition in definitions:
                                   word_to_definitions[word].add(definition)

    # Convert sets to sorted lists
    final_dict = {
        word: sorted(list(definitions))
        for word, definitions in word_to_definitions.items()
    }

    print(f"Total valid words collected: {len(final_dict)}")
    return final_dict


# -------------------------------
# MAIN
# -------------------------------

def main():
    directory = os.getcwd() + "/word_net_2025"

    print("Building synset map...")
    synset_map = build_synset_map(directory)

    print("Building word dictionary...")
    dictionary = build_dictionary(directory, synset_map)

    print(f"Writing output to {OUTPUT_FILE}...")
    with open(OUTPUT_FILE, "w", encoding="utf-8") as f:
        json.dump(dictionary, f, ensure_ascii=False, indent = 2)

    print("Done.")


if __name__ == "__main__":
    main()