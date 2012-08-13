ALTER TABLE mb_forms ADD COLUMN deployed_ind BOOLEAN DEFAULT FALSE;
ALTER TABLE mb_forms ADD COLUMN abstract_ind BOOLEAN DEFAULT FALSE;

ALTER TABLE mb_forms ADD COLUMN companyid VARCHAR(255);

ALTER TABLE mb_reportlist ADD COLUMN companyid VARCHAR(255);

alter table mb_form_config add column primaryField varchar(50);

update mb_form_config set primaryField='id';

alter table mb_reportlist add column shared int;

set foreign_key_checks = 0;


#****************Clear module builder related tables*****************

 delete from mb__comments                                                 ;
 delete from mb__prereq                                                   ;
 delete from mb__prereqgroup                                              ;
 delete from mb__prereqgroupmap                                           ;
 delete from mb__prereqmap                                                ;
 delete from mb__renderer                                                 ;
 delete from mb_btnpermmap                                                ;
 delete from mb_buttonconf                                                ;
 delete from mb_comboFilterConfig                                         ;
 delete from mb_configmaster                                              ;
 delete from mb_configmasterdata                                          ;
 delete from mb_dashboard                                                 ;
 delete from mb_dashlinks                                                 ;
 delete from mb_dashportlet                                               ;
 delete from mb_docs                                                      ;
 delete from mb_docsmap                                                   ;
 delete from mb_form_config                                               ;
 delete from mb_forms                                                     ;
 delete from mb_gridconfig                                                ;
 delete from mb_linkgroup                                                 ;
 delete from mb_moduleConfigMap                                           ;
 delete from mb_modulegr                                                  ;
 delete from mb_permactions                                               ;
 delete from mb_permgrmaster                                              ;
 delete from mb_permmaster                                                ;
 delete from mb_processChart                                              ;
 delete from mb_reportlist                                                ;
 delete from mb_rolegrmaster                                              ;
 delete from mb_rolemaster                                                ;
 delete from mb_roleperm                                                  ;
 delete from mb_stdConfigs                                                ;
 delete from userrolemapping						  ;

#************Insert Default Data********************
insert into mb__renderer (id,name,rendererValue,isstatic) values (0, 'None', 'None', '');

insert into mb_stdConfigs values(1,'Comments','');
insert into mb_stdConfigs values(2,'Documents','');

insert into mb_permactions values(1,'Button');
insert into mb_permactions values(2,'Add Record');
insert into mb_permactions values(3,'Update Record');
insert into mb_permactions values(4,'Delete Record');
insert into mb_permactions values(5,'Add comment');
insert into mb_permactions values(6,'Delete comment');
insert into mb_permactions values(7,'Add document');
insert into mb_permactions values(8,'Delete document');
insert into mb_permactions values(9,'Module View Tab');

insert into mb_rolegrmaster values(1, '', 'Company Administrator');
insert into mb_rolegrmaster values(2, '', 'Sales Manager');
insert into mb_rolegrmaster values(3, '', 'Sales Executive');

insert into mb_rolemaster values(1,1,'','Admin');
insert into mb_rolemaster values(2,2,'','Manager');
insert into mb_rolemaster values(3,3,'','Employee');

insert into mb_linkgroup values(1,'Quick Links');
#insert into mb_linkgroup values(2,'Shortcut Links');

insert into mb_dashboard values(1,'01');


#***********Make User Module Entry****** Replace @createdby i.e. 99f1eb77-cac9-41bb-977b-c8bc17fb3daa with Admin userid **********

INSERT INTO mb_reportlist (reportid, deleteflag, reportkey, `type`, createddate, modifieddate, createdby, reportname, tablename, displayconf, tableflag, companyid, shared) VALUES ('1', 0, 0, 0, '2010-12-24 17:38:56.0', '2010-12-24 17:39:08.0', '99f1eb77-cac9-41bb-977b-c8bc17fb3daa', 'User', 'users', 0, 0, NULL, 1);
INSERT INTO mb_gridconfig (id, columnindex, hidden, filter, reftable, xtype, displayfield, `name`, combogridconfig, countflag, summaryType, defaultValue, reportid, renderer) VALUES ('u1', 0, false, NULL, NULL, 'textfield', 'Name', 'usersX_Xfname', '-1', false, NULL, NULL, '1', '0');
INSERT INTO mb_gridconfig (id, columnindex, hidden, filter, reftable, xtype, displayfield, `name`, combogridconfig, countflag, summaryType, defaultValue, reportid, renderer) VALUES ('u2', 1, true, NULL, NULL, 'default', NULL, 'usersX_Xuserid', '-1', false, NULL, NULL, '1', '0');
insert into mb_form_config (moduleid, primaryField) values('1','userid');


#**************Import crm forms************Replace @todb i.e. crmstaging and @fromdb i.e. accprod **********

insert into crmstaging.mb_form_config (select * from accprod.mb_form_config where moduleid in ( select reportid from accprod.mb_reportlist where reportid = '1'));
insert into crmstaging.mb_configmaster (select * from accprod.mb_configmaster where configid in ('c357c1042cf29ed1012cf397f85e0002','c357c1042cf29ed1012cf39bee2c0006','c357c1042cf29ed1012cf39c9a1a0007','c357c1042cf29ed1012cf39cb15d0008','c357c1042cf29ed1012cf39d280c0009','c357c1042cf29ed1012cf39e4e28000a','c357c1042cf29ed1012cf39ecec3000b','c357c1042cf29ed1012cf3a0029b000c','c357c1042cf29ed1012cf3a017ce000d','c357c1042cf29ed1012cf3a116b4000e','c357c1042cf29ed1012cf3a12b1e000f','c357c1042cf29ed1012cf3a3e4050010','c357c1042cf29ed1012cf3a5f4520011','c357c1042cf29ed1012cf3a606c20012','c357c1042cf29ed1012cf3a623bc0013','c357c1042cf29ed1012cf3abdb070014','c357c1042cf29ed1012cf3abf79c0015'));
insert into crmstaging.mb_configmasterdata (select * from accprod.mb_configmasterdata where configid in ('c357c1042cf29ed1012cf397f85e0002','c357c1042cf29ed1012cf39bee2c0006','c357c1042cf29ed1012cf39c9a1a0007','c357c1042cf29ed1012cf39cb15d0008','c357c1042cf29ed1012cf39d280c0009','c357c1042cf29ed1012cf39e4e28000a','c357c1042cf29ed1012cf39ecec3000b','c357c1042cf29ed1012cf3a0029b000c','c357c1042cf29ed1012cf3a017ce000d','c357c1042cf29ed1012cf3a116b4000e','c357c1042cf29ed1012cf3a12b1e000f','c357c1042cf29ed1012cf3a3e4050010','c357c1042cf29ed1012cf3a5f4520011','c357c1042cf29ed1012cf3a606c20012','c357c1042cf29ed1012cf3a623bc0013','c357c1042cf29ed1012cf3abdb070014','c357c1042cf29ed1012cf3abf79c0015'));
insert into crmstaging.mb_reportlist (select * from accprod.mb_reportlist where reportname like 'crm%');
insert into crmstaging.mb_forms (formid, name, data, deployed_ind, abstract_ind, moduleid, companyid) (select formid, name, data, deployed_ind, abstract_ind, moduleid, companyid from accprod.mb_forms where moduleid in ( select reportid from accprod.mb_reportlist where reportname like 'crm%'));
insert into crmstaging.mb_form_config (select * from accprod.mb_form_config where moduleid in ( select reportid from accprod.mb_reportlist where reportname like 'crm%'));
insert into crmstaging.mb_gridconfig (select * from accprod.mb_gridconfig where reportid in ( select reportid from accprod.mb_reportlist where reportname like 'crm%'));
insert into crmstaging.mb_permgrmaster(permgrid, description, permgrname, num, reportid, taskflag) (select permgrid, description, permgrname, num, reportid, taskflag from accprod.mb_permgrmaster where reportid in ( select reportid from accprod.mb_reportlist where reportname like 'crm%'));
insert into crmstaging.mb_permmaster (select * from accprod.mb_permmaster where permgrid in (select permgrid from accprod.mb_permgrmaster where reportid in ( select reportid from accprod.mb_reportlist where reportname like 'crm%')));

update mb_forms set deployed_ind = false where moduleid in ( select reportid from accprod.mb_reportlist where reportname like 'crm%');

update mb_gridconfig set renderer = 0;

#*********** Map Users with roles in module builder - Execute Following Query *********************
#http://localhost:8084/HQLCrm/jspfiles/mapUserRole_MB.jsp?fromdb=crmstaging

set foreign_key_checks = 1;

alter table  mb_configmasterdata change masterid id varchar(255);

