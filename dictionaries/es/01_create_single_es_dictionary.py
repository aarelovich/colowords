import json
import unicodedata
from collections import defaultdict

INPUT = "/Users/ViewMind/AndroidStudioProjects/colowords/dictionaries/es/raw-wiktextract-data.jsonl"
OUTPUT = "/Users/ViewMind/AndroidStudioProjects/colowords/dictionaries/es/es_dictionary_raw.json"

MIN_LEN = 3
MAX_LEN = 7

def norm(s):
	# Normalize accents consistently (NFC is usually what you want for display/storage)
	return unicodedata.normalize("NFC", s)

def valid_word(w):
	if not (MIN_LEN <= len(w) <= MAX_LEN):
		return False
	if " " in w:
		return False
	if not w.isalpha():  # keeps ñ á é í ó ú ü
		return False
	if not w.islower():
		return False
	return True

defs_by_word = defaultdict(set)

with open(INPUT, "r", encoding="utf-8") as f:
	for i, line in enumerate(f, 1):
		if not line.strip():
			continue

		try:
			entry = json.loads(line)
		except json.JSONDecodeError:
			continue

		# Keep only Spanish entries
		if entry.get("lang_code") != "es":
			continue

		word = entry.get("word")
		if not isinstance(word, str):
			continue

		word = norm(word.strip())
		if not valid_word(word):
			continue

		senses = entry.get("senses", [])
		if not isinstance(senses, list):
			continue

		for s in senses:
			if not isinstance(s, dict):
				continue
			glosses = s.get("glosses", [])
			if not isinstance(glosses, list):
				continue
			for g in glosses:
				if isinstance(g, str):
					g = " ".join(norm(g).split())
					if g:
						defs_by_word[word].add(g)

		# Progress every 1M lines (optional)
		if i % 1_000_000 == 0:
			print(f"Processed {i:,} lines, words so far: {len(defs_by_word):,}")

# Convert sets to sorted lists for JSON
final = {w: sorted(list(ds)) for w, ds in defs_by_word.items()}

with open(OUTPUT, "w", encoding="utf-8") as out:
	json.dump(final, out, ensure_ascii=False, indent=2, sort_keys=True)

print(f"Done. Words: {len(final):,}")
print(f"Wrote: {OUTPUT}")