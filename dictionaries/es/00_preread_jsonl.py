import json

with open("/Users/ViewMind/AndroidStudioProjects/colowords/dictionaries/es/raw-wiktextract-data.jsonl", "r", encoding="utf-8") as f:
    count = 0
    for line in f:
        entry = json.loads(line)

        # Only Spanish entries
        if entry.get("lang") == "Spanish":
            print(json.dumps(entry, indent=2, ensure_ascii=False))
            print("\n---\n")
            count += 1

        if count == 3:
            break