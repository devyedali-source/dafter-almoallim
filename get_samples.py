import json

with open('planning_weekly_audit.json', 'r', encoding='utf-8') as f:
    data = json.load(f)

items = data['weeklyItems']

# Find multi-week topics
multi_week_groups = {}
for it in items:
    key = (it['level'], it['subject'], it['title'])
    if key not in multi_week_groups:
        multi_week_groups[key] = []
    multi_week_groups[key].append(it['week'])

print("--- MULTI-WEEK TOPIC EXAMPLES ---")
count = 0
for (lvl, subj, title), weeks in multi_week_groups.items():
    if len(weeks) > 1 and len(set(weeks)) > 1:
        count += 1
        print(f"EXAMPLE {count}:")
        print(f"LEVEL: {lvl}AP")
        print(f"SUBJECT: {subj}")
        print(f"TOPIC: {title}")
        print(f"WEEKS: {sorted(list(set(weeks)))}")
        print(f"NUMBER OF WEEKLY INSTANCES: {len(weeks)}")
        print()
        if count >= 10:
            break

# Find pedagogical stations
print("--- PEDAGOGICAL STATION EXAMPLES ---")
ped_stations = [it for it in items if it['category'] == 'PEDAGOGICAL_STATION']
for idx, ps in enumerate(ped_stations[:10], 1):
    # previous learning items
    prev = [p['title'] for p in items if p['level'] == ps['level'] and p['subject'] == ps['subject'] and p['week'] is not None and p['week'] < ps['week'] and p['category'] == 'REGULAR_LESSON']
    print(f"EXAMPLE {idx}:")
    print(f"LEVEL: {ps['level']}AP")
    print(f"TERM: {ps['term']}")
    print(f"SUBJECT: {ps['subject']}")
    print(f"DOMAIN: {ps['domain']}")
    print(f"COMPETENCY: {ps['competency'][:80]}...")
    print(f"WEEK: {ps['week']}")
    print(f"PREVIOUS LEARNING ITEMS: {prev[-3:] if prev else ['المكتسبات القبلية والتشخيصية']}")
    print()
