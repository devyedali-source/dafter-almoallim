import re
import json
import os

def parse_kotlin_file_robust(file_path):
    if not os.path.exists(file_path):
        print(f"File not found: {file_path}")
        return []
    with open(file_path, 'r', encoding='utf-8') as f:
        content = f.read()

    # Find each AnnualLessonItem( ... ) by tracking balanced parentheses
    items = []
    marker = "AnnualLessonItem("
    idx = 0
    while True:
        pos = content.find(marker, idx)
        if pos == -1:
            break
        
        # Start of arguments
        start_args = pos + len(marker)
        depth = 1
        in_string = False
        escape = False
        end_pos = start_args
        
        for i in range(start_args, len(content)):
            ch = content[i]
            if in_string:
                if escape:
                    escape = False
                elif ch == '\\':
                    escape = True
                elif ch == '"':
                    in_string = False
            else:
                if ch == '"':
                    in_string = True
                elif ch == '(':
                    depth += 1
                elif ch == ')':
                    depth -= 1
                    if depth == 0:
                        end_pos = i
                        break
        
        block = content[start_args:end_pos]
        idx = end_pos + 1
        
        # Extract fields using regex on block
        def get_field_str(fname):
            # match fname = "..."
            pattern = re.compile(r'\b' + fname + r'\s*=\s*"((?:[^"\\]|\\.)*)"', re.DOTALL)
            m = pattern.search(block)
            if m:
                return m.group(1).replace(r'\"', '"').replace(r'\n', '\n').replace(r'\t', '\t')
            return ""

        def get_field_int(fname):
            m = re.search(r'\b' + fname + r'\s*=\s*(\d+)', block)
            if m:
                return int(m.group(1))
            return None

        def get_field_bool(fname):
            m = re.search(r'\b' + fname + r'\s*=\s*(true|false)', block, re.IGNORECASE)
            if m:
                return m.group(1).lower() == 'true'
            return False

        item_id = get_field_str('id')
        level = get_field_int('level')
        week = get_field_int('week')
        week_end = get_field_int('weekEnd')
        if week_end is None and week is not None:
            week_end = week
            
        subject = get_field_str('subject')
        domain = get_field_str('domain')
        title = get_field_str('title')
        competency = get_field_str('competency')
        skills = get_field_str('skills')
        activities = get_field_str('activities')
        item_type = get_field_str('type')
        is_french = get_field_bool('isFrench')

        if level is not None and item_id:
            items.append({
                "id": item_id,
                "level": level,
                "week": week,
                "weekEnd": week_end,
                "subject": subject,
                "domain": domain,
                "title": title,
                "competency": competency,
                "skills": skills,
                "activities": activities,
                "type": item_type,
                "isFrench": is_french
            })

    return items

def classify_item(item, week):
    t = (item.get("type") or "").strip().upper()
    title = (item.get("title") or "").strip()
    subject = (item.get("subject") or "").strip()

    if t == "HOLIDAY" or "عطلة" in title or "عطلة" in subject:
        return "HOLIDAY_OR_EXAM", "HOLIDAY"
    if (t == "EXAM" or "اختبار" in title or "اختبار" in subject or
        "امتحان" in title or "امتحان" in subject or
        "مسابقة" in title or "مسابقة" in subject or
        "مداولات" in title or "مداولات" in subject or
        "فرز النتائج" in title or "اختتام السنة" in title):
        return "HOLIDAY_OR_EXAM", "EXAM"
    if (t in ["INTEGRATION_ASSESSMENT_REMEDIATION", "DIAGNOSTIC"] or
        "إدماج" in title or "إدماج" in subject or
        "تقويم" in title or "تقويم" in subject or
        "علاج" in title or "علاج" in subject or
        "تشخيص" in title or "تشخيص" in subject or
        "محطة" in title or "محطة" in subject):
        return "PEDAGOGICAL_STATION", t if t else "PEDAGOGICAL_STATION"
    
    return "REGULAR_LESSON", "REGULAR_LESSON"

def get_term_for_week(week):
    if week is None:
        return 1
    if 1 <= week <= 13:
        return 1
    elif 14 <= week <= 26:
        return 2
    else:
        return 3

def main():
    repo_path = 'app/src/main/java/com/example/data/planning/AnnualPlanningRepository.kt'
    l5_path = 'app/src/main/java/com/example/data/planning/Level5OfficialCurriculum.kt'
    
    repo_items = parse_kotlin_file_robust(repo_path)
    l5_items = parse_kotlin_file_robust(l5_path)
    
    all_raw_items = [it for it in repo_items if it['level'] in [1, 2, 3, 4]] + l5_items
    
    print(f"Robust parsed raw items: {len(all_raw_items)} (L1-4: {len([i for i in all_raw_items if i['level']<5])}, L5: {len(l5_items)})")

    weekly_instances = []
    
    for item in all_raw_items:
        w_start = item['week']
        w_end = item['weekEnd'] if item['weekEnd'] is not None else w_start
        
        if w_start is None:
            category, specific_type = classify_item(item, 0)
            weekly_instances.append({
                "level": item['level'],
                "term": 0,
                "week": None,
                "subWeekIndex": 0,
                "totalWeeksSpan": 0,
                "instanceId": f"{item['id']}_wNone",
                "originalId": item['id'],
                "subject": item['subject'],
                "domain": item['domain'],
                "title": item['title'],
                "competency": item['competency'],
                "skills": item['skills'],
                "activities": item['activities'],
                "category": category,
                "specificType": specific_type,
                "isFrench": item['isFrench']
            })
            continue

        span = max(1, w_end - w_start + 1)
        for idx, w in enumerate(range(w_start, w_end + 1)):
            category, specific_type = classify_item(item, w)
            term = get_term_for_week(w)
            weekly_instances.append({
                "level": item['level'],
                "term": term,
                "week": w,
                "subWeekIndex": idx,
                "totalWeeksSpan": span,
                "instanceId": f"{item['id']}_w{w}",
                "originalId": item['id'],
                "subject": item['subject'],
                "domain": item['domain'],
                "title": item['title'],
                "competency": item['competency'],
                "skills": item['skills'],
                "activities": item['activities'],
                "category": category,
                "specificType": specific_type,
                "isFrench": item['isFrench']
            })

    print(f"Robust expanded weekly instances: {len(weekly_instances)}")

    # Anomaly checks
    anomalies = {
        "missing_topics": [],
        "missing_weeks": [],
        "missing_domains": [],
        "missing_competencies": [],
        "missing_knowledge": [],
        "missing_skills": [],
        "duplicate_week_keys": [],
        "suspicious_holiday_with_content": [],
        "unlinked_pedagogical_stations": []
    }

    seen_keys = {}
    for inst in weekly_instances:
        if not inst['title'].strip():
            anomalies["missing_topics"].append(inst['instanceId'])
        
        if inst['week'] is None:
            anomalies["missing_weeks"].append(inst['instanceId'])
            
        if not inst['domain'].strip():
            anomalies["missing_domains"].append(inst['instanceId'])

        if not inst['competency'].strip():
            anomalies["missing_competencies"].append(inst['instanceId'])

        if not inst['title'].strip() and not inst['domain'].strip():
            anomalies["missing_knowledge"].append(inst['instanceId'])

        if not inst['skills'].strip():
            anomalies["missing_skills"].append(inst['instanceId'])

        # Duplicate check: exact same level, term, subject, domain, week, title
        key = (inst['level'], inst['term'], inst['subject'], inst['domain'], inst['week'], inst['title'])
        if key in seen_keys:
            anomalies["duplicate_week_keys"].append({
                "key": f"L{inst['level']}_T{inst['term']}_{inst['subject']}_W{inst['week']}_{inst['title'][:30]}",
                "instance1": seen_keys[key],
                "instance2": inst['instanceId']
            })
        else:
            seen_keys[key] = inst['instanceId']

    by_level = {}
    by_subject_detailed = {}
    
    for lvl in [1, 2, 3, 4, 5]:
        lvl_insts = [i for i in weekly_instances if i['level'] == lvl]
        terms_data = {}
        for t in [1, 2, 3]:
            t_insts = [i for i in lvl_insts if i['term'] == t]
            reg = len([i for i in t_insts if i['category'] == 'REGULAR_LESSON'])
            ped = len([i for i in t_insts if i['category'] == 'PEDAGOGICAL_STATION'])
            hol_exam = len([i for i in t_insts if i['category'] == 'HOLIDAY_OR_EXAM'])
            terms_data[f"term_{t}"] = {
                "regular_lessons": reg,
                "pedagogical_stations": ped,
                "holiday_or_exam": hol_exam,
                "total_teaching_instances": reg + ped
            }
        
        reg_total = len([i for i in lvl_insts if i['category'] == 'REGULAR_LESSON'])
        ped_total = len([i for i in lvl_insts if i['category'] == 'PEDAGOGICAL_STATION'])
        hol_exam_total = len([i for i in lvl_insts if i['category'] == 'HOLIDAY_OR_EXAM'])
        
        by_level[f"{lvl}AP"] = {
            "terms": terms_data,
            "total_regular_lessons": reg_total,
            "total_pedagogical_stations": ped_total,
            "total_holiday_or_exam": hol_exam_total,
            "total_teaching_instances": reg_total + ped_total,
            "total_all_instances": len(lvl_insts)
        }

        # Subject breakdown
        subjects = sorted(list(set(i['subject'] for i in lvl_insts)))
        lvl_subjects = {}
        for sub in subjects:
            s_insts = [i for i in lvl_insts if i['subject'] == sub]
            s_reg = len([i for i in s_insts if i['category'] == 'REGULAR_LESSON'])
            s_ped = len([i for i in s_insts if i['category'] == 'PEDAGOGICAL_STATION'])
            s_hol = len([i for i in s_insts if i['category'] == 'HOLIDAY_OR_EXAM'])
            
            domains = sorted(list(set(i['domain'] for i in s_insts)))
            dom_data = {}
            for dom in domains:
                d_insts = [i for i in s_insts if i['domain'] == dom]
                d_reg = len([i for i in d_insts if i['category'] == 'REGULAR_LESSON'])
                d_ped = len([i for i in d_insts if i['category'] == 'PEDAGOGICAL_STATION'])
                
                # Competencies under domain
                comps = {}
                for c_inst in d_insts:
                    c_text = c_inst['competency']
                    if c_text not in comps:
                        comps[c_text] = {
                            "competency_text": c_text,
                            "teaching_instances_count": 0,
                            "weeks": []
                        }
                    if c_inst['category'] in ['REGULAR_LESSON', 'PEDAGOGICAL_STATION']:
                        comps[c_text]["teaching_instances_count"] += 1
                    comps[c_text]["weeks"].append(c_inst['week'])

                dom_data[dom] = {
                    "regular_lessons": d_reg,
                    "pedagogical_stations": d_ped,
                    "total_teaching_instances": d_reg + d_ped,
                    "weeks": sorted(list(set(i['week'] for i in d_insts if i['week'] is not None))),
                    "competencies": list(comps.values())
                }

            lvl_subjects[sub] = {
                "regular_lessons": s_reg,
                "pedagogical_stations": s_ped,
                "holiday_or_exam": s_hol,
                "total_teaching_instances": s_reg + s_ped,
                "domains": dom_data
            }
        by_subject_detailed[f"{lvl}AP"] = lvl_subjects

    total_reg = sum(by_level[f"{lvl}AP"]["total_regular_lessons"] for lvl in [1, 2, 3, 4, 5])
    total_ped = sum(by_level[f"{lvl}AP"]["total_pedagogical_stations"] for lvl in [1, 2, 3, 4, 5])
    total_teaching = sum(by_level[f"{lvl}AP"]["total_teaching_instances"] for lvl in [1, 2, 3, 4, 5])
    total_hol_exam = sum(by_level[f"{lvl}AP"]["total_holiday_or_exam"] for lvl in [1, 2, 3, 4, 5])

    summary = {
        "by_level": by_level,
        "by_subject_detailed": by_subject_detailed,
        "overall": {
            "total_regular_lessons": total_reg,
            "total_pedagogical_stations": total_ped,
            "total_teaching_instances": total_teaching,
            "total_holiday_or_exam": total_hol_exam,
            "total_instances": total_teaching + total_hol_exam
        },
        "anomalies_count": {k: len(v) for k, v in anomalies.items()},
        "anomalies_samples": {k: v[:5] for k, v in anomalies.items()}
    }

    with open('audit_summary.json', 'w', encoding='utf-8') as f:
        json.dump(summary, f, ensure_ascii=False, indent=2)

    for lvl in [1, 2, 3, 4, 5]:
        lvl_insts = [i for i in weekly_instances if i['level'] == lvl]
        with open(f'audit_{lvl}AP.json', 'w', encoding='utf-8') as f:
            json.dump({
                "level": f"{lvl}AP",
                "summary": by_level[f"{lvl}AP"],
                "subjects": by_subject_detailed[f"{lvl}AP"],
                "instances": lvl_insts
            }, f, ensure_ascii=False, indent=2)

    with open('planning_weekly_audit.json', 'w', encoding='utf-8') as f:
        json.dump({
            "summaryByLevel": by_level,
            "summaryBySubject": by_subject_detailed,
            "weeklyItems": weekly_instances,
            "anomalies": anomalies,
            "overall": summary["overall"]
        }, f, ensure_ascii=False, indent=2)

    print("ALL AUDIT FILES GENERATED SUCCESSFULLY!")
    print(f"1AP: {by_level['1AP']['total_teaching_instances']} teaching instances (Reg: {by_level['1AP']['total_regular_lessons']}, Ped: {by_level['1AP']['total_pedagogical_stations']}, Hol/Exam: {by_level['1AP']['total_holiday_or_exam']})")
    print(f"2AP: {by_level['2AP']['total_teaching_instances']} teaching instances (Reg: {by_level['2AP']['total_regular_lessons']}, Ped: {by_level['2AP']['total_pedagogical_stations']}, Hol/Exam: {by_level['2AP']['total_holiday_or_exam']})")
    print(f"3AP: {by_level['3AP']['total_teaching_instances']} teaching instances (Reg: {by_level['3AP']['total_regular_lessons']}, Ped: {by_level['3AP']['total_pedagogical_stations']}, Hol/Exam: {by_level['3AP']['total_holiday_or_exam']})")
    print(f"4AP: {by_level['4AP']['total_teaching_instances']} teaching instances (Reg: {by_level['4AP']['total_regular_lessons']}, Ped: {by_level['4AP']['total_pedagogical_stations']}, Hol/Exam: {by_level['4AP']['total_holiday_or_exam']})")
    print(f"5AP: {by_level['5AP']['total_teaching_instances']} teaching instances (Reg: {by_level['5AP']['total_regular_lessons']}, Ped: {by_level['5AP']['total_pedagogical_stations']}, Hol/Exam: {by_level['5AP']['total_holiday_or_exam']})")
    print(f"TOTAL TEACHING: {total_teaching} (Reg: {total_reg}, Ped: {total_ped}, Hol/Exam: {total_hol_exam})")
    print(f"Anomalies: {json.dumps({k: len(v) for k, v in anomalies.items()})}")

if __name__ == '__main__':
    main()
