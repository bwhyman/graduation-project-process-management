/* process_score process_id命中索引 */

select ps.id,
json_array(json_object('teacherId', ps.id, 'processId', ps.process_id)) as details
from process_score ps