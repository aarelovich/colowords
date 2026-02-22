import json
import unicodedata
from wordfreq import zipf_frequency

# Tune this. Typical good starting points:
# 4.5 = very common words only (easiest)
# 4.2 = common + some medium (good balance)
# 4.0 = larger pool (harder / more variety)

word = "reo";
score = zipf_frequency(word, "es")
print(f"{word}: {score}");