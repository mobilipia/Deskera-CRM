/*
 * Copyright (C) 2012  Krawler Information Systems Pvt Ltd
 * All rights reserved.
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
*/
package com.krawler.spring.comments;

import com.krawler.common.admin.User;
import com.krawler.common.service.ServiceException;
import com.krawler.common.util.StringUtil;
import com.krawler.common.admin.CaseComment;
import com.krawler.common.admin.Comment;
import com.krawler.common.admin.NewComment;
import com.krawler.common.util.KWLErrorMsgs;
import com.krawler.dao.BaseDAO;
import com.krawler.spring.common.KwlReturnObject;
import com.krawler.utils.json.base.JSONObject;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author Karthik
 */
public class commentDAOImpl extends BaseDAO implements commentDAO {

    /* (non-Javadoc)
     * @see com.krawler.spring.comments.commentDAO#getComments(java.util.HashMap)
     */
    public KwlReturnObject getComments(HashMap requestParams) throws ServiceException {
        List ll = null;
        int dl = 0;
        try {
            ArrayList filter_params = new ArrayList();
            if(requestParams.containsKey("recid") && requestParams.get("recid") != null){
                filter_params.add(requestParams.get("recid").toString());
            }
            filter_params.add(false);
            String Hql = " FROM Comment c  where c.leadid=? and c.deleted = ? order by c.postedon desc";
            ll = executeQuery(Hql, filter_params.toArray());
            dl = ll.size();
        } catch (Exception e) {
            throw ServiceException.FAILURE("crmCommentDAOImpl.getComments : " + e.getMessage(), e);
        }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }
    
    
    
    
    public KwlReturnObject getCaseComments(HashMap requestParams) throws ServiceException {
        List ll = null;
        int dl = 0;
        try {
            ArrayList filter_params = new ArrayList();
            if(requestParams.containsKey("recid") && requestParams.get("recid") != null){
                filter_params.add(requestParams.get("recid").toString());
            }
            filter_params.add(false);
            String Hql = " FROM CaseComment c  where c.caseid=? and c.deleted = ? order by c.postedon desc";
            ll = executeQuery(Hql, filter_params.toArray());
            dl = ll.size();
        } catch (Exception e) {
            throw ServiceException.FAILURE("crmCommentDAOImpl.getCaseComments : " + e.getMessage(), e);
        }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }

    /* (non-Javadoc)
     * @see com.krawler.spring.comments.commentDAO#deleteComments(java.lang.String, java.lang.String)
     */
    public KwlReturnObject deleteComments(String userid, String id) throws ServiceException {
        int dl = 0;
        List ll = null;
        try {
            String Hqldelcom = "from NewComment c where c.userId.userID= ? and c.commentid.Id=?";
            ll = executeQuery(Hqldelcom, new Object[]{userid, id});
            dl = ll.size();
            if (dl > 0) {
                NewComment cnw = (NewComment) ll.get(0);
                delete(cnw);
            }
        } catch (Exception e) {
            throw ServiceException.FAILURE("crmCommentDAOImpl.deleteComments : " + e.getMessage(), e);
        }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }

    public KwlReturnObject deleteOriginalComment(String id) throws ServiceException {
        int dl = 0;
        List ll = null;
        try {
            String Hqldelcom = "from Comment c where c.Id = ?";
            ll = executeQuery(Hqldelcom, new Object[]{id});
            dl = ll.size();
            if (dl > 0) {
                Comment comment = (Comment) ll.get(0);
                comment.setDeleted(true);
                save(comment);
            }
        } catch (Exception e) {
            throw ServiceException.FAILURE("crmCommentDAOImpl.deleteOriginalComment : " + e.getMessage(), e);
        }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }
    
    
    
    public KwlReturnObject deleteCaseComment(String id) throws ServiceException {
        int dl = 0;
        List ll = null;
        try {
            String Hqldelcom = "from CaseComment c where c.Id = ?";
            ll = executeQuery(Hqldelcom, new Object[]{id});
            dl = ll.size();
            if (dl > 0) {
            	CaseComment caseComment = (CaseComment) ll.get(0);
            	caseComment.setDeleted(true);
                save(caseComment);
            }
        } catch (Exception e) {
            throw ServiceException.FAILURE("crmCommentDAOImpl.deleteCaseComment : " + e.getMessage(), e);
        }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }

    /* (non-Javadoc)
     * @see com.krawler.spring.comments.commentDAO#addComments(com.krawler.utils.json.base.JSONObject)
     */
    public KwlReturnObject addComments(JSONObject jobj) throws ServiceException {

        List ll = null;
        int dl = 0;
        try {
            Comment crmcomment = new Comment();
            String companyid = jobj.getString("companyid");
            String userid = jobj.getString("userid");
            String cid = jobj.getString("cid");
            
            if(jobj.has("userid")) {
                crmcomment.setuserId((User) get(User.class, userid));
            }
            if(jobj.has("refid")) {
                crmcomment.setleadid(jobj.getString("refid"));
            }
            if(jobj.has("id")) {
                crmcomment.setId(jobj.getString("id"));
            }
            if(jobj.has("comment")) {
                crmcomment.setComment(jobj.getString("comment"));
            }
            if(jobj.has("mapid")) {
                crmcomment.setRelatedto(jobj.getString("mapid"));
            }
            crmcomment.setPostedon(new Date().getTime());

            save(crmcomment);

            insertCommentUserMapping(crmcomment, companyid, userid, cid);
            
        } catch (Exception e) {
            throw ServiceException.FAILURE("crmCommentDAOImpl.addComments : " + e.getMessage(), e);
        }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }
    
    
    public Object[] getCustomerName(String contactid) throws ServiceException {
        
        
    	String Hql = "select c.firstname,c.lastname from CrmContact c where c.contactid=?";
        List ll = executeQuery(Hql, contactid);
        if(ll.isEmpty())
    	return null;
    return (Object[])ll.get(0);
    
    
}
    
    public KwlReturnObject addCaseComments(JSONObject jobj) throws ServiceException {

        List ll = null;
        int dl = 0;
        try {
        	CaseComment caseComment = new CaseComment();
        	if(jobj.has("companyid")) {
        		String companyid = jobj.getString("companyid");
            }
            
            if(jobj.has("cid")) {
            	String cid = jobj.getString("cid");
            }
            
            if(jobj.has("userid")) {
            	caseComment.setuserId(jobj.getString("userid"));
            }
            if(jobj.has("refid")) {
            	caseComment.setCaseid(jobj.getString("refid"));
            }
            if(jobj.has("id")) {
            	caseComment.setId(jobj.getString("id"));
            }
            if(jobj.has("comment")) {
            	caseComment.setComment(jobj.getString("comment"));
            }
            if(jobj.has("mapid")) {
            	caseComment.setRelatedto(jobj.getString("mapid"));
            }
            caseComment.setPostedon(new Date().getTime());
            if(jobj.has("deleted")) {
            	caseComment.setDeleted(jobj.getBoolean("deleted"));
            }
            if(jobj.has("userflag")) {
            	caseComment.setUserflag('1');
            }else{
            caseComment.setUserflag('2');
            }

            save(caseComment);

           // insertCommentUserMapping(crmcomment, companyid, userid, cid);
            
        } catch (Exception e) {
            throw ServiceException.FAILURE("crmCommentDAOImpl.addCaseComments : " + e.getMessage(), e);
        }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }

    /* (non-Javadoc)
     * @see com.krawler.spring.comments.commentDAO#editComments(com.krawler.utils.json.base.JSONObject)
     */
    public KwlReturnObject editComments(JSONObject jobj) throws ServiceException {

        List ll = null;
        int dl = 0;
        try {
            String id = "";
            String companyid = jobj.getString("companyid");
            String userid = jobj.getString("userid");
            String cid = jobj.getString("cid");
            if(jobj.has("id")) {
                id = jobj.getString("id");
            }
            Comment crmcomment = (Comment) get(Comment.class, id);

            if(jobj.has("userid")) {
                crmcomment.setuserId((User) get(User.class, userid));
            }
            if(jobj.has("refid")) {
                crmcomment.setleadid(jobj.getString("refid"));
            }
            if(jobj.has("id")) {
                crmcomment.setId(jobj.getString("id"));
            }
            if(jobj.has("comment")) {
                crmcomment.setComment(jobj.getString("comment"));
            }
            if(jobj.has("mapid")) {
                crmcomment.setRelatedto(jobj.getString("mapid"));
            }
            crmcomment.setUpdatedon(new Date().getTime());

            save(crmcomment);

            // delete comment-user mapping
            deleteCommentUserMapping(id);

            // Insert comment-user mapping
            insertCommentUserMapping(crmcomment, companyid, userid, cid);
            
        } catch (Exception e) {
            throw ServiceException.FAILURE("crmCommentDAOImpl.addComments : " + e.getMessage(), e);
        }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }
    
    public KwlReturnObject editCaseComments(JSONObject jobj) throws ServiceException {

        List ll = null;
        int dl = 0;
        try {
            String id = "";
            String companyid = jobj.getString("companyid");
           // String userid = jobj.getString("userid");
            String cid = jobj.getString("cid");
            if(jobj.has("id")) {
                id = jobj.getString("id");
            }
            CaseComment CaseComment = (CaseComment) get(CaseComment.class, id);

            if(jobj.has("userid")) {
            	CaseComment.setuserId(jobj.getString("userid"));
            }
            if(jobj.has("refid")) {
            	CaseComment.setCaseid(jobj.getString("refid"));
            }
            if(jobj.has("id")) {
            	CaseComment.setId(jobj.getString("id"));
            }
            if(jobj.has("comment")) {
            	CaseComment.setComment(jobj.getString("comment"));
            }
            if(jobj.has("mapid")) {
            	CaseComment.setRelatedto(jobj.getString("mapid"));
            }
            CaseComment.setUpdatedon(new Date().getTime());

            save(CaseComment);

            // delete comment-user mapping
         //   deleteCommentUserMapping(id);

            // Insert comment-user mapping
           // insertCommentUserMapping(crmcomment, companyid, userid, cid);
            
        } catch (Exception e) {
            throw ServiceException.FAILURE("crmCommentDAOImpl.addComments : " + e.getMessage(), e);
        }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }

     public void insertCommentUserMapping(Comment crmcomment,String companyid,String userid ,String cid)  {
            List ll = null;
            int dl = 0;
            String Hqlc = " FROM User c  where   c.company.companyID= ? ";
            ll = executeQuery(Hqlc, new Object[]{companyid});
            dl = ll.size();
            Iterator ite = ll.iterator();
            while (ite.hasNext()) {
                User obj = (User) ite.next();
                if (!userid.equals(obj.getUserID())) {
                    NewComment crmnewcomment = new NewComment();
                    crmnewcomment.setuserId((User) get(User.class, obj.getUserID()));
                    crmnewcomment.setCid(cid);
                    crmnewcomment.setCommentid(crmcomment);
                    save(crmnewcomment);
                }
            }
    }

    public void deleteCommentUserMapping(String commentid)  {

        String hql="delete from NewComment c where c.commentid.Id = ? ";
        executeUpdate(hql, commentid);
    }
    /* (non-Javadoc)
     * @see com.krawler.spring.comments.commentDAO#getCommentsCountForRecord(java.util.HashMap)
     */
    public Object getCommentsCountForRecord(HashMap requestParams) throws ServiceException {
        Object count = 0;
        try {
            ArrayList filter_params = new ArrayList();
            filter_params.add(false);
            filter_params.add(requestParams.get("companyid").toString());
            String filterQuery = "";
            if(requestParams.containsKey("recid") && requestParams.get("recid") != null){
                filter_params.add(requestParams.get("recid").toString());
                filterQuery = " and c.leadid=? ";
            }
            String Hql = "Select count(c.Id) as count FROM Comment c where c.deleted = ? and c.userId.company.companyID = ? "+filterQuery+" group by c.leadid";
            List ll = executeQuery(Hql, filter_params.toArray());
            Iterator ite = ll.iterator();
            if(ite.hasNext()) {
                count = (Object) ite.next();
            }
        } catch (Exception e) {
            throw ServiceException.FAILURE("crmCommentDAOImpl.getCommentsCountForRecord : " + e.getMessage(), e);
        }
        return count;
    }

    public HashMap<String, String> getTotalCommentsCount(List<String> list, String companyid) throws ServiceException {
        HashMap<String, String> commentCountMap = new HashMap<String, String>();
        Object count = 0;
        try {
            String filterQuery = "";
            List<List> paramll = new ArrayList();
            List<String> paramnames = new ArrayList();
            if(!list.isEmpty()){
                filterQuery = " and c.leadid in (:recordlist) ";
                paramll.add(list);
                paramnames.add("recordlist");
            }
            String Hql = "Select count(c.Id) as count, c.leadid FROM Comment c where c.deleted = false and c.userId.company.companyID = '"+companyid+"' "+filterQuery+" group by c.leadid";
            List<Object[]> ll = executeCollectionQuery(Hql,paramnames,paramll);
            for(Object[] row : ll) {
                count = (Object) row[0];
                commentCountMap.put(row[1].toString(), String.valueOf(count));
            }
        } catch (Exception e) {
            throw ServiceException.FAILURE("crmCommentDAOImpl.getTotalCommentsCount : " + e.getMessage(), e);
        }
        return commentCountMap;
    }

    
    public HashMap<String, String> getTotalCaseCommentsCount(List<String> list, String companyid) throws ServiceException {
        HashMap<String, String> commentCountMap = new HashMap<String, String>();
        Object count = 0;
        try {
            String filterQuery = "";
            List<List> paramll = new ArrayList();
            List<String> paramnames = new ArrayList();
            if(!list.isEmpty()){
                filterQuery = " and c.caseid in (:recordlist) ";
                paramll.add(list);
                paramnames.add("recordlist");
            }
            String Hql = "Select count(c.Id) as count, c.caseid FROM CaseComment c where c.deleted = false "+filterQuery+" group by c.caseid";
            List<Object[]> ll = executeCollectionQuery(Hql,paramnames,paramll);
            for(Object[] row : ll) {
                count = (Object) row[0];
                commentCountMap.put(row[1].toString(), String.valueOf(count));
            }
        } catch (Exception e) {
            throw ServiceException.FAILURE("crmCommentDAOImpl.getTotalCaseCommentsCount : " + e.getMessage(), e);
        }
        return commentCountMap;
    }
    
    

    /* (non-Javadoc)
     * @see com.krawler.spring.comments.commentDAO#getNewCommentCount(java.lang.String)
     */
    public HashMap<String, String> getNewCommentCount(String userid) throws ServiceException {
        Object count = 0;
        HashMap<String, String> commentCountMap = new HashMap<String, String>();
        try {
            String Hql = "Select count(distinct nc.commentid) as count, nc.commentid.leadid from NewComment nc " +
                    "where nc.userId.userID = ? and nc.commentid.deleted = ? group by nc.commentid.leadid";
            List<Object[]> lst = executeQuery(Hql, new Object[]{userid,false});
            for (Object[] row : lst) {
                count = (Object) row[0];
                commentCountMap.put(row[1].toString(), count+"");
            }
        } catch (Exception e) {
            throw ServiceException.FAILURE(e.getMessage(), e);
        }
        return commentCountMap;
    }

    /* (non-Javadoc)
     * @see com.krawler.spring.comments.commentDAO#getAllCommentList(java.lang.String, java.lang.String)
     */
    public JSONObject getAllCommentList(String userid, String parentid) throws ServiceException {
        int count = 0;
        int newCommentdl = 0;
        int dl = 0;
        JSONObject jtemp = new JSONObject();
        try {
            String Hql = " FROM Comment c  where c.leadid=? and c.deleted = false ";
            List<Comment> lst = executeQuery(Hql, new Object[]{parentid});
            dl = lst.size();
            jtemp.put("totalcomment", dl);
            for (Comment t : lst) {
                String newCommentHql = "from NewComment  where  commentid.Id=? and userId.userID=?";
                List ncomlst = executeQuery(newCommentHql, new Object[]{t.getId(), userid});
                newCommentdl = ncomlst.size();
                if (newCommentdl > 0) {
                    count++;
                }
            }
            jtemp.put("newcomment", count);
        } catch (Exception e) {
            throw ServiceException.FAILURE(e.getMessage(), e);
        }
        return jtemp;
    }

    /* (non-Javadoc)
     * @see com.krawler.spring.comments.commentDAO#CreateDuplicateComments(java.lang.String, java.lang.String)
     */
    public void CreateDuplicateComments(String recordId,String newRecordId)  {
            List<Comment> ll = null;
            DetachedCriteria crit = DetachedCriteria.forClass(Comment.class);
            crit.add(Restrictions.eq("leadid", recordId));
            crit.add(Restrictions.eq("deleted", false));
            ll = findByCriteria(crit);
            for( Comment commentObj : ll ){
                Comment commentObj1 = new Comment();
                commentObj1.setId(java.util.UUID.randomUUID().toString());
                commentObj1.setComment(commentObj.getComment());
                commentObj1.setleadid(newRecordId);
                commentObj1.setuserId(commentObj.getuserId());
                commentObj1.setRelatedto(commentObj.getRelatedto());
                commentObj1.setPostedon(commentObj.getPostedon());
                save(commentObj1);
            }
    }

     public Map<String, List<String>> getCommentz(List<String> recordids)
    {
        Map<String, List<String>> commentMap = new HashMap<String, List<String>>();
        if (recordids != null && !recordids.isEmpty())
        {
            StringBuilder query = new StringBuilder("select c.leadid, c.comment, c from Comment c where c.deleted = false and c.leadid in (");

            for (String recordid: recordids)
            {
                query.append('\'');
                query.append(recordid);
                query.append('\'');
                query.append(',');
            }

            query.deleteCharAt(query.length() - 1);
            query.append(')');

            List<Object[]> results = getHibernateTemplate().find(query.toString());

            if (results != null)
            {
                for (Object[] result: results)
                {
                    String recid = (String) result[0];

                    if (commentMap.containsKey(recid))
                    {
                        List<String> ownerList = commentMap.get(recid);
                        ownerList.add((String)result[1]);
                    }
                    else
                    {
                        List<String> commentList = new ArrayList<String>();
                        commentList.add((String)result[1]);
                        commentMap.put(recid, commentList);
                    }
                }
            }
        }
        return commentMap;
    }
    
     
     public Map<String, List<String>> getCaseCommentz(List<String> recordids)
     {
         Map<String, List<String>> commentMap = new HashMap<String, List<String>>();
         if (recordids != null && !recordids.isEmpty())
         {
             StringBuilder query = new StringBuilder("select c.caseid, c.comment, c from CaseComment c where c.deleted = false and c.caseid in (");

             for (String recordid: recordids)
             {
                 query.append('\'');
                 query.append(recordid);
                 query.append('\'');
                 query.append(',');
             }

             query.deleteCharAt(query.length() - 1);
             query.append(')');

             List<Object[]> results = getHibernateTemplate().find(query.toString());

             if (results != null)
             {
                 for (Object[] result: results)
                 {
                     String recid = (String) result[0];

                     if (commentMap.containsKey(recid))
                     {
                         List<String> ownerList = commentMap.get(recid);
                         ownerList.add((String)result[1]);
                     }
                     else
                     {
                         List<String> commentList = new ArrayList<String>();
                         commentList.add((String)result[1]);
                         commentMap.put(recid, commentList);
                     }
                 }
             }
         }
         return commentMap;
     }
}

