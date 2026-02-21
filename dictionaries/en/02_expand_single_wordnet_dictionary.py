import json
from lemminflect import getInflection, getAllInflections
from collections import defaultdict
from better_profanity import profanity

# WE need to try to take out offensive words. 
profanity.load_censor_words()

MIN_LEN = 3
MAX_LEN = 8

INPUT_FILE = "single_wordnet_dictionary.json"
OUTPUT_FILE = "single_wordnet_dictionary_expanded.json"

BANNED_ROOTS = {
    "fuck",
    "shit",
    "bitch",
    "ass",
    "cunt",
    "dick",
    "cock",
    "piss",
    "slut",
    "whore"
}


def valid_word(word):
    is_valid = (
        MIN_LEN <= len(word) <= MAX_LEN
        and word.isalpha()
        and word.islower()
    )

    if (not is_valid):
        return False

    # The other condition for validity is taht the the word does NOT contained any of the banned root words.
    for root in BANNED_ROOTS:
        if root in word:
            return False

    return True


def expand_dictionary():
    with open(INPUT_FILE, "r", encoding="utf-8") as f:
        base_dict = json.load(f)

    expanded_dict = defaultdict(set)

    for lemma, definitions in base_dict.items():

        # Always keep original lemma
        if valid_word(lemma) and not profanity.contains_profanity(lemma):
            for d in definitions:
                expanded_dict[lemma].add(d)

        # Generate inflections (verbs + nouns)
        all_forms = getAllInflections(lemma)

        for pos_tag, forms in all_forms.items():
            for form in forms:

                form = form.lower()

                if not valid_word(form):
                    continue

                if (profanity.contains_profanity(form)):
                    continue

                for d in definitions:
                    expanded_dict[form].add(d)

    # Convert sets to sorted lists
    final_dict = {
        word: sorted(list(defs))
        for word, defs in expanded_dict.items()
    }

    print(f"Final word count after expansion: {len(final_dict)}")

    with open(OUTPUT_FILE, "w", encoding="utf-8") as f:
        json.dump(final_dict, f, ensure_ascii=False, indent=2, sort_keys=True)


if __name__ == "__main__":
    expand_dictionary()