/* process_score process_id命中索引 */
explain
select * from process_score ps, process p
where ps.process_id=p.id and p.department_id='1296708598482489344'