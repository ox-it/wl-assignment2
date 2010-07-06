alter table A2_ASSIGNMENT_T add column MODEL_ANSWER_ENABLED number(1,0);
alter table A2_ASSIGNMENT_T add column MODEL_ANSWER_TEXT clob;
alter table A2_ASSIGNMENT_T add column MODEL_ANSWER_DISPLAY_RULE number(10,0);

-- fill in MODEL_ANSWER_ENABLED with 0
update A2_ASSIGNMENT_T set MODEL_ANSWER_ENABLED = 0;
update A2_ASSIGNMENT_T set MODEL_ANSWER_DISPLAY_RULE = 0;

alter table A2_ASSIGN_ATTACH_T add column ASSIGN_ATTACH_TYPE varchar2(1) not null;

update A2_ASSIGN_ATTACH_T set ASSIGN_ATTACH_TYPE = 'A';

create index ASSIGN_ATTACH_TYPE_I on A2_ASSIGN_ATTACH_T (ASSIGN_ATTACH_TYPE);


