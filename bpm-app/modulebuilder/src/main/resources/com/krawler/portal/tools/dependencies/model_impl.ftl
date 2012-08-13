package ${packagePath};

/**
 * <a href="${entity.getName()}Model.java.html"><b><i>View Source</i></b></a>
 *
 * <p>
 * ServiceBuilder generated this class. Modifications in this class will be
 * overwritten the next time is generated.
 * </p>
 *
 * <p>
 * This interface is a model that represents the <code>${entity.name}</code>
 * table in the database.
 * </p>
 *
 */
import javax.servlet.http.HttpServletRequest;
import org.hibernate.*;
import java.util.*;
import com.krawler.utils.json.base.JSONObject;
import com.krawler.common.service.ServiceException;
import com.krawler.esp.utils.PropsValues;
import com.krawler.esp.handlers.notification;
import com.krawler.formbuilder.servlet.FormServlet;

public class ${entity.getName()} {

    /*Before record insert in module builder*/
    <#if entity.getReportFlag() >
        <#if entity.getCreateTableFlag() >
            public static boolean beforeInsert(Session session,HttpServletRequest request,Object object) {
                ${entity.getClassName()} modObject = (${entity.getClassName()}) object;
                notification.sendMessage(session, request, "${entity.getClassName()}", modObject.getId(), 0);
                return true;
            }

            /*After record insert in module builder*/
            public static boolean afterInsert(Session session,HttpServletRequest request,Object object) {
                ${entity.getClassName()} modObject = (${entity.getClassName()}) object;
                notification.sendMessage(session, request, "${entity.getClassName()}", modObject.getId(), 1);
                return true;
            }
        </#if>
            /*Before record edit in module builder*/
            public static boolean beforeEdit(Session session,HttpServletRequest request,Object object) {
                <#if entity.getCreateTableFlag() >
                    ${entity.getClassName()} modObject = (${entity.getClassName()}) object;
                    notification.sendMessage(session, request, "${entity.getClassName()}", modObject.getId(), 2);
                </#if>
                return true;
            }

            /*After record edit in module builder*/
            public static boolean afterEdit(Session session,HttpServletRequest request,Object object) {
                <#if entity.getCreateTableFlag() >
                    ${entity.getClassName()} modObject = (${entity.getClassName()}) object;
                    notification.sendMessage(session, request, "${entity.getClassName()}", modObject.getId(), 3);
                </#if>
                return true;
            }

            /*Before combo load in report builder*/
            public static String beforeComboLoad(Session session,HttpServletRequest request,Object[] paramArray, String filterQuery, String subQuery, String[] colArray) {
                String retQuery = "";
                /*No. of parameter in subQuery and colArray should be equal and in the same order.
                  Append subquery in select clause e.g. Select tablename.id, + subquery + from ....
                  Append filterQuery in where clause. Use paramArray for parametarised query.*/
                return retQuery;
            }

            /*Before record delete in grid builder*/
            public static boolean beforeDelete(Session session,HttpServletRequest request,Object object) {
                <#if entity.getCreateTableFlag() >
                    ${entity.getClassName()} modObject = (${entity.getClassName()}) object;
                    notification.sendMessage(session, request, "${entity.getClassName()}", modObject.getId(), 4);
                </#if>
                return true;
            }

            /*After record delete in grid builder*/
            public static boolean afterDelete(Session session,HttpServletRequest request,Object object) {
                <#if entity.getCreateTableFlag() >
                    ${entity.getClassName()} modObject = (${entity.getClassName()}) object;
                    notification.sendMessage(session, request, "${entity.getClassName()}", modObject.getId(), 5);
                </#if>
                return true;
            }

            /*Before grid data load*/
            public static void beforeGridLoadData(Session session,HttpServletRequest request,String finalQuery,String fieldQuery,
                    String delQuery,String searchFilter,String ruleFilterQuery,String sortQuery, String reportTName,
                    ArrayList<String> fieldNameArray,ArrayList<String> countFieldNameArray, Boolean commentFlag, Boolean docFlag, JSONObject jobj, Boolean pagingFlag, Object[] paramArray) throws ServiceException {
                try {
                    int start = 0;
                    int limit = 0;
                    if(pagingFlag) {
                        start = Integer.parseInt(request.getParameter("start"));
                        limit = Integer.parseInt(request.getParameter("limit"));
                    }
                    mb_reportlist basemodule = (mb_reportlist) session.load(mb_reportlist.class, request.getParameter("reportid"));
                    finalQuery = fieldQuery + finalQuery + delQuery+searchFilter + ruleFilterQuery+sortQuery;
                    List ls = HibernateUtil.executeQuery(session, finalQuery, paramArray);
                    int count = ls.size();
                    if(pagingFlag) {
                        ls = HibernateUtil.executeQueryPaging(session, finalQuery, paramArray, new Integer[] {start, limit});
                    }

                    Iterator ite = ls.iterator();
                    while(ite.hasNext()) {
                        Object[] row = (Object[]) ite.next();
                        JSONObject jtemp = new JSONObject();
                        String idVal = "";
                        Object invoker = null;
                        boolean condFlag = true;
                        for(int i = 0; i < row.length; i++) {
                            Class cl1 = row[i].getClass();

                            String clStr = cl1.getName();
                            pm_taskmaster taskObj = (pm_taskmaster) session.load(pm_taskmaster.class, request.getParameter("taskid"));

                            boolean conditionFlag = true;
                            if(!clStr.equals("java.lang.Boolean") && !clStr.equals("java.lang.Double") && !clStr.equals("java.lang.Float")
                                    && !clStr.equals("java.lang.Integer") && !clStr.equals("java.lang.Long") && !clStr.equals("java.lang.Short") && !clStr.equals("java.lang.String")) {
                                conditionFlag = FormServlet.checkFilterRules(session, taskObj, basemodule, row[i], 2);
                                if(conditionFlag) {
                                    jtemp.put(fieldNameArray.get(i), row[i]);
                                }
                            } else {
                                jtemp.put(fieldNameArray.get(i), row[i]);
                            }

                            if(!conditionFlag) {
                                condFlag = false;
                                break;
                            }

                            String reportTName1 = reportTName+PropsValues.REPORT_HARDCODE_STR+"id";
                            if(fieldNameArray.get(i).equals(reportTName1)) {
                                idVal = row[i].toString();
                                String tablename = PropsValues.PACKAGE_PATH+"."+reportTName;
                                Class cl = Class.forName(tablename);
                                invoker = session.load(cl, idVal);
                            }
                        }

                        if(!condFlag) {
                            continue;
                        }
                        //Get count, comment, docoment
                        reportMethods.getCountCommentDocuments(session, countFieldNameArray, reportTName, invoker,
                                commentFlag, docFlag, idVal, jtemp);

                        jobj.append("data", jtemp);
                    }
                } catch(Exception e) {
                    throw ServiceException.FAILURE("reportMethods.loadData", e);
                }
            }

            /*Before grid export data*/
            public static void beforeGridExportData(Session session,HttpServletRequest request,String finalQuery,String fieldQuery,
                    String delQuery,String ruleFilterQuery,String reportTName,ArrayList<String> displayFieldArray,
                    ArrayList<String> fieldNameArray,ArrayList<String> countFieldNameArray, Boolean commentFlag, Boolean docFlag, JSONObject jobj) throws ServiceException {
                try {
                    finalQuery = fieldQuery + finalQuery + delQuery + ruleFilterQuery;
                    List ls = HibernateUtil.executeQuery(session, finalQuery);
                    int count = ls.size();
                    ls = HibernateUtil.executeQuery(session, finalQuery);
                    Iterator ite = ls.iterator();
                    while(ite.hasNext()) {
                        Object[] row = (Object[]) ite.next();
                        JSONObject jtemp = new JSONObject();
                        String idVal = "";
                        Object invoker = null;
                        for(int i = 0; i < row.length; i++) {
    //                        jtemp.put(fieldNameArray.get(i), row[i]);
                            jtemp.put(displayFieldArray.get(i), row[i]);
                            String reportTName1 = reportTName+PropsValues.REPORT_HARDCODE_STR+"id";
                            if(fieldNameArray.get(i).equals(reportTName1)) {
                                idVal = row[i].toString();
                                String tablename = PropsValues.PACKAGE_PATH+"."+reportTName;
                                Class cl = Class.forName(tablename);
                                invoker = session.load(cl, idVal);
                            }
                        }
                        //Get count, comment, docoment
                        reportMethods.getCountCommentDocuments(session, countFieldNameArray, reportTName, invoker,
                                commentFlag, docFlag, idVal, jtemp);

                        jobj.append("data", jtemp);
                    }
                } catch(Exception e) {
                    throw ServiceException.FAILURE("reportMethods.loadData", e);
                }
            }
    <#else>
        public static boolean beforeInsert(Session session,HttpServletRequest request, HashMap<String,String> arrParam,Object object) {
            ${entity.getClassName()} modObject = (${entity.getClassName()}) object;
            notification.sendMessage(session, request, "${entity.getClassName()}", modObject.getId(), 0);
            return true;
        }

        /*After record insert in module builder*/
        public static boolean afterInsert(Session session,HttpServletRequest request, HashMap<String,String> arrParam,Object object) {
            ${entity.getClassName()} modObject = (${entity.getClassName()}) object;
            notification.sendMessage(session, request, "${entity.getClassName()}", modObject.getId(), 1);
            return true;
        }

        /*Before record edit in module builder*/
        public static boolean beforeEdit(Session session,HttpServletRequest request, HashMap<String,String> arrParam,Object object) {
            ${entity.getClassName()} modObject = (${entity.getClassName()}) object;
            notification.sendMessage(session, request, "${entity.getClassName()}", modObject.getId(), 2);
            return true;
        }

        /*After record edit in module builder*/
        public static boolean afterEdit(Session session,HttpServletRequest request, HashMap<String,String> arrParam,Object object) {
            ${entity.getClassName()} modObject = (${entity.getClassName()}) object;
            notification.sendMessage(session, request, "${entity.getClassName()}", modObject.getId(), 3);
            return true;
        }

        /*Before combo load in module builder*/
        public static String beforeComboLoad(Session session,HttpServletRequest request,Object[] paramArray, String filterQuery, String cascadeQuery) {
            String retQuery = "";
            /*Append filterQuery + cascadeQuery to the main query
              if filter rules and cascading is set to the selected combo.
              Use paramArray for parametarised query.*/
            return retQuery;
        }

        /*Before record delete in module builder*/
        public static boolean beforeDelete(Session session,HttpServletRequest request,Object object) {
            ${entity.getClassName()} modObject = (${entity.getClassName()}) object;
            notification.sendMessage(session, request, "${entity.getClassName()}", modObject.getId(), 4);
            return true;
        }

        /*After record delete in module builder*/
        public static boolean afterDelete(Session session,HttpServletRequest request,Object object) {
            ${entity.getClassName()} modObject = (${entity.getClassName()}) object;
            notification.sendMessage(session, request, "${entity.getClassName()}", modObject.getId(), 5);
            return true;
        }
    </#if>
}
