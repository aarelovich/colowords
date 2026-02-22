import json
import unicodedata
from wordfreq import zipf_frequency

INPUT_FILE = "/Users/ViewMind/AndroidStudioProjects/colowords/dictionaries/es/es_dictionary_raw.json"
OUTPUT_FILE = "/Users/ViewMind/AndroidStudioProjects/colowords/dictionaries/es/es.json"

# Tune this. Typical good starting points:
# 4.5 = very common words only (easiest)
# 4.2 = common + some medium (good balance)
# 4.0 = larger pool (harder / more variety)
# I selected 3.1 to have some more 3 letter words. 
MIN_ZIPF = 3.1

def norm(s):
	return unicodedata.normalize("NFC", s)

# Spanish profanity filtering:
# - This is intentionally substring-based to catch compounds/derivatives.
# - Keep it short and editable; add/remove roots as you see leaks.
BANNED_ROOTS = {
	# Common strong profanity / sexual slurs
	"mierd", "puta", "puto", "putt", "put@", "put4",
	"coño", "conch", "cogid", "coger" ,  # note: "coger" is normal in some regions; remove if you want it allowed
	"verga", "pija", "pij@", "pene", "vagin",
	"culo", "culia", "culiao", "culiao", "culer",
	"joder", "follar",
	"cabrón", "cabron",
	"chota", "concha",
	"polla", "teta", "tetón", "teton",
	"pajer", "paja", "masturb",
	"porno", "porn",
	"zorra",

	# Insults / hate-ish terms (you decide how strict to be)
	"maric", "putit", "gilipoll", "imbecil", "idiot", "estupid", "bolud", "pelotud",

	# English profanity leaks in Spanish dumps
	"fuck", "shit", "bitch", "ass", "cunt", "dick", "cock", "piss", "slut", "whore"
}

def is_clean_word(w):
	# word already lowercase alpha, but we normalize anyway
	w = norm(w)

	# fast path: common safe letters only
	# (optional) if you want to reject rare letters or diacritics, do it elsewhere

	for root in BANNED_ROOTS:
		if root in w:
			return False
	return True

def filter_dict():
	with open(INPUT_FILE, "r", encoding="utf-8") as f:
		data = json.load(f)

	kept = {}
	n_total = len(data)
	n_bad = 0
	n_rare = 0

	for word, defs in data.items():
		word = norm(word)

		# profanity filter
		if not is_clean_word(word):
			n_bad += 1
			continue

		# frequency filter
		score = zipf_frequency(word, "es")
		if score < MIN_ZIPF:
			n_rare += 1
			continue

		# keep (defs are already lists)
		kept[word] = defs

	print(f"Total words:   {n_total:,}")
	print(f"Removed bad:   {n_bad:,}")
	print(f"Removed rare:  {n_rare:,}  (zipf < {MIN_ZIPF})")
	print(f"Kept:          {len(kept):,}")

	with open(OUTPUT_FILE, "w", encoding="utf-8") as out:
		json.dump(kept, out, ensure_ascii=False, indent=2, sort_keys=True)

	print(f"Wrote: {OUTPUT_FILE}")

if __name__ == "__main__":
	filter_dict()