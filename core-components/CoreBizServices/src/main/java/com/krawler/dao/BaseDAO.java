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
package com.krawler.dao;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.krawler.common.query.*;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.DetachedCriteria;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.krawler.common.util.BaseStringUtil;
import com.krawler.spring.common.KwlReturnObject;
import java.io.Serializable;
import java.sql.SQLException;
import org.hibernate.HibernateException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.rowset.SqlRowSet;

/**
 * @author Johnson
 * 
 *         This Class should be extended by all the DAO's.
 */
public class BaseDAO extends HibernateDaoSupport {
        private JdbcTemplate jdbcTemplate;

        public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
            this.jdbcTemplate = jdbcTemplate;
        }
        /**
         * Immediately loads the object using the given class identity
         * @param entityClass class for object to load
         * @param id unique identifier of persistent object
         * @return the persistent object
         */
        public Object get(Class entityClass, Serializable id) {
            return getHibernateTemplate().get(entityClass, id);
        }

	/**
	 * Saves the Hibernate Entity.
	 * 
	 * @param entity
	 *            The entity to persist
	 * @return identity of persistent object
	 */
	public Serializable save(Object entity) {
		return getHibernateTemplate().save(entity);
	}

	/**
	 * Saves all the entities of a specific type in the provided collection
	 * 
	 * @param entities
	 */
	public void saveAll(Collection entities) {
		getHibernateTemplate().saveOrUpdateAll(entities);
	}

	/**
	 * Saves or Updates the Hibernate Entity.
	 *
	 * @param entity The entity to persist
	 */
	public void saveOrUpdate(Object entity) {
		 getHibernateTemplate().saveOrUpdate(entity);
	}

	/**
	 * Executes the provided HQL query after applying the provided parameters
	 * and returns the result
	 * 
	 * @param hql
	 *            The query to execute
	 * @param params
	 *            Query Paramters
	 * @return List
	 */
	public List executeQuery(String hql, Object[] params) {
		List results = null;
		results = getHibernateTemplate().find(hql, params);
		return results;
	}

	/**
	 * Executes the provided HQL query after applying the provided parameter and
	 * returns the result
	 * 
	 * @param hql
	 *            The query to execute
	 * @param param
	 *            Query Paramter
	 * @return List
	 */
	public List executeQuery(String hql, Object param) {
		Object[] params = { param };
		return executeQuery(hql, params);
	}

	/**
	 * Executes the provided HQL query
	 * 
	 * @param hql
	 *            The query to execute
	 * @return List
	 */
	public List executeQuery(String hql) {
		return executeQuery(hql, null);
	}

        /**
	 * Executes the provided HQL query after applying the provided collection parameter and
	 *
         * returns the result

         * @param hql
	 *            The query to execute
         * @param paramnames
	 *            Collection Query Paramters names
         * @param params
         *            Collection Query Paramters values
         * @return List
	 */
        public List executeCollectionQuery(final String hql,final List<String> paramnames,final List<List> params) {
		List results = null;
		results = getHibernateTemplate().executeFind(new HibernateCallback() {
			public Object doInHibernate(Session session) {
				Query query = session.createQuery(hql);
					for (int i = 0; i < paramnames.size(); i++) {
						query.setParameterList(paramnames.get(i), params.get(i));
					}
				return query.list();
			}
		});
		return results;
	}
	/**
	 * Executes a limit select query using the provided query, parameters and
	 * limits. Limits are provided using the pagingParam parameter.
	 * 
	 * @param hql
	 *            The query to execute
	 * @param params
	 *            Query Paramter
	 * @param pagingParam
	 *            Limit paramters. The first entry is the lower limit and the
	 *            second is the upper limit
	 * @return List
	 */
	public List executeQueryPaging(final String hql, final Object[] params, final Integer[] pagingParam) {
		List results = null;
		results = getHibernateTemplate().executeFind(new HibernateCallback() {
			public Object doInHibernate(Session session) {
				Query query = session.createQuery(hql);

				if (params != null) {
					for (int i = 0; i < params.length; i++) {
						query.setParameter(i, params[i]);
					}
				}
				query.setFirstResult(pagingParam[0]);
				query.setMaxResults(pagingParam[1]);

				return query.list();
			}
		});
		return results;
	}

	/**
	 * Executes a limit select query using the provided query and limits. Limits
	 * are provided using the pagingParam parameter.
	 * 
	 * @param hql
	 *            The query to execute
	 * @param pagingParam
	 *            Limit paramters. The first entry is the lower limit and the
	 *            second is the upper limit
	 * @return List
	 */
	public List executeQueryPaging(String hql, Integer[] pagingParam) {
		return executeQueryPaging(hql, null, pagingParam);
	}

	/**
	 * Executes an update query using the provided hql and query parameters
	 * 
	 * @param hql
	 *            Query to execute
	 * @param params
	 *            the query paramters
	 * @return List
	 */
	public int executeUpdate(final String hql, final Object[] params) {
		int numRow = 0;
		numRow = (Integer) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) {
				int numRows = 0;
				Query query = session.createQuery(hql);
				if (params != null) {
					for (int i = 0; i < params.length; i++) {
						query.setParameter(i, params[i]);
					}
				}
				numRows = query.executeUpdate();
				return numRows;
			}
		});
		return numRow;
	}

	public List executeQuery(final String hql, final Object[] params, final Map<String, Object> namedParams) {
		return getHibernateTemplate().executeFind(new HibernateCallback() {
			public Object doInHibernate(Session session) {
				Query query = session.createQuery(hql);
				if (params != null) {
					for (int i = 0; i < params.length; i++) {
							query.setParameter(i, params[i]);
					}
				}
				if (namedParams != null) {
					for (Map.Entry<String, Object> entry:namedParams.entrySet()) {
						Object value = entry.getValue();
						if(value!=null){ 
							if(value instanceof Collection)
								query.setParameterList(entry.getKey(), (Collection)value);
							else if(entry.getValue().getClass().isArray())
								query.setParameterList(entry.getKey(), (Object[])value);
							else
								query.setParameter(entry.getKey(), entry.getValue());
						}else
							query.setParameter(entry.getKey(), entry.getValue());
					}
				}
				return query.list();
			}
		});
	}
	
	public List executeQuery(final String hql, final Object[] params, final Map<String, Object> namedParams, final int[] pagingParams) {
		return getHibernateTemplate().executeFind(new HibernateCallback() {
			public Object doInHibernate(Session session) {
				Query query = session.createQuery(hql);
				if (params != null) {
					for (int i = 0; i < params.length; i++) {
							query.setParameter(i, params[i]);
					}
				}
				if (namedParams != null) {
					for (Map.Entry<String, Object> entry:namedParams.entrySet()) {
						Object value = entry.getValue();
						if(value!=null){ 
							if(value instanceof Collection)
								query.setParameterList(entry.getKey(), (Collection)value);
							else if(entry.getValue().getClass().isArray())
								query.setParameterList(entry.getKey(), (Object[])value);
							else
								query.setParameter(entry.getKey(), entry.getValue());
						}else
							query.setParameter(entry.getKey(), entry.getValue());
					}
				}
				if(pagingParams!=null){
					query.setFirstResult(pagingParams[0]);
					query.setMaxResults(pagingParams[1]);
				}
				return query.list();
			}
		});
	}
	/**
	 * Executes an update query using the provided hql and query parameters
	 * 
	 * @param hql
	 *            Query to execute
	 * @param params
	 *            the query paramters
	 * @return List
	 */
	public int executeUpdate(final String hql, final Object[] params, final Map<String, Object> namedParams) {
		int numRow = 0;
		numRow = (Integer) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) {
				int numRows = 0;
				Query query = session.createQuery(hql);
				if (params != null) {
					for (int i = 0; i < params.length; i++) {
							query.setParameter(i, params[i]);
					}
				}
				if (namedParams != null) {
					for (Map.Entry<String, Object> entry:namedParams.entrySet()) {
						Object value = entry.getValue();
						if(value!=null){ 
							if(value instanceof Collection)
								query.setParameterList(entry.getKey(), (Collection)value);
							else if(entry.getValue().getClass().isArray())
								query.setParameterList(entry.getKey(), (Object[])value);
							else
								query.setParameter(entry.getKey(), entry.getValue());
						}else
							query.setParameter(entry.getKey(), entry.getValue());
					}
				}
				numRows = query.executeUpdate();
				return numRows;
			}
		});
		return numRow;
	}
	/**
	 * Executes an update query using the provided hql, query parameters and
	 * paging parameters
	 * 
	 * @param hql
	 *            Query to execute
	 * @param params
	 *            Query parameters
	 * @param pagingParam
	 *            paging parameters
	 * @return List
	 */
	public int executeUpdatePaging(final String hql, final Object[] params, final Integer[] pagingParam) {
		int numRow = 0;
		numRow = (Integer) getHibernateTemplate().execute(new HibernateCallback() {

			public Object doInHibernate(Session session) {
				int numRows = 0;
				Query query = session.createQuery(hql);
				if (params != null) {
					for (int i = 0; i < params.length; i++) {
						query.setParameter(i, params[i]);
					}
				}
				query.setFirstResult(pagingParam[0]);
				query.setMaxResults(pagingParam[1]);
				numRows = query.executeUpdate();
				session.flush();
				session.clear();
				return numRows;
			}
		});

		return numRow;
	}

	/**
	 * Executes an update query using the provided hql and query parameter
	 * 
	 * @param hql
	 *            Query to execute
	 * @param param
	 *            Query Parameter
	 * @return List
	 */
	public int executeUpdate(String hql, Object param) {
		Object[] params = { param };
		return executeUpdate(hql, params);
	}

	/**
	 * Executes an update query using the provided hql and query parameter
	 * 
	 * @param sql
	 *            Query to execute
	 * @return List
	 */
	public int executeUpdate(String sql) {
		return executeUpdate(sql, null);
	}

	/**
	 * @param requestParams
	 * @param classstr
	 * @param primarykey
	 * @return
	 * @throws ClassNotFoundException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 * @throws InvocationTargetException
	 * @throws IllegalArgumentException
	 */
	public Object setterMethod(HashMap<String, Object> requestParams, String classstr, String primarykey)
			throws ClassNotFoundException, InstantiationException, IllegalAccessException, SecurityException,
			NoSuchMethodException, IllegalArgumentException, InvocationTargetException {
		Object obj = null;
		Class cl = Class.forName(classstr);
		if (requestParams.get(primarykey) != null) {
			obj = getHibernateTemplate().get(cl, requestParams.get(primarykey).toString());
			if (obj == null) {
				obj = cl.newInstance();
			}
		} else {
			obj = cl.newInstance();
			Method setter = cl.getMethod("set" + primarykey, String.class);
			String id = UUID.randomUUID().toString();
			setter.invoke(obj, id);
		}
		for (Object key : requestParams.keySet()) {
			Class rettype = cl.getMethod("get" + key).getReturnType();
			Method setter = cl.getMethod("set" + key, rettype);
			if (rettype.isPrimitive() || rettype.equals(String.class) || rettype.equals(Date.class)
					|| rettype.equals(Integer.class) || rettype.equals(Boolean.class)) {

                if(rettype.equals(String.class) && requestParams.get(key)!=null && requestParams.get(key).getClass().equals(Double.class)) {
                    setter.invoke(obj, String.valueOf(requestParams.get(key)));
                }else{
                    setter.invoke(obj, requestParams.get(key));
                }
			} else {
				setter.invoke(obj, getHibernateTemplate().get(rettype, requestParams.get(key).toString()));
			}
		}
		getHibernateTemplate().save(obj);

		return obj;
	}

	/**
	 * @param queryParams
	 * @param allFlag
	 * @return 
	 */
	public KwlReturnObject getTableData(HashMap<String, Object> queryParams, boolean allFlag) {
		List ll = null;
		int dl = 0;
		String tableName = queryParams.get("table_name").toString();
		String userListParam = queryParams.get("userlist_param").toString();
		String userListVal = queryParams.get("userlist_value").toString();
		ArrayList filter_names = (ArrayList) queryParams.get("filter_names");
		ArrayList filter_values = (ArrayList) queryParams.get("filter_values");
		ArrayList order_by = null;
		ArrayList order_type = null;
		if (queryParams.containsKey("order_by"))
			order_by = (ArrayList) queryParams.get("order_by");
		if (queryParams.containsKey("order_type"))
			order_type = (ArrayList) queryParams.get("order_type");

		String Hql = "select c from " + tableName + " c ";
		String filterQuery = BaseStringUtil.filterQuery(filter_names, "where") + " and " + userListParam + " in ("
				+ userListVal + ")";
		Hql += filterQuery;

		String orderQuery = BaseStringUtil.orderQuery(order_by, order_type);
		Hql += orderQuery;

		ll = executeQuery(Hql, filter_values.toArray());
		dl = ll.size();
		if (!allFlag) {
			int start = Integer.parseInt(queryParams.get("start").toString());
			int limit = Integer.parseInt(queryParams.get("limit").toString());
			ll = executeQueryPaging(Hql, filter_values.toArray(), new Integer[] { start, limit });
		}

		return new KwlReturnObject(true, "002", "", ll, dl);
	}
	
	/**
	 * @param criteria
	 * @return
	 */
	public List findByCriteria(DetachedCriteria criteria){
		return getHibernateTemplate().findByCriteria(criteria);
	}

    public List executeNativeQuery(String query) {
        return executeNativeQuery(query, new Object[]{});
    }

    public List executeNativeQuery(String query, Object param) {
        return executeNativeQuery(query, new Object[]{param});
    }

    /**
     * executes the native SQL query
     *
     * @param query the given query string
     * @param params the parameters to pass in the query
     * @return the list of records (rows)
     */
    public List executeNativeQuery(String query, Object[] params) {
        HibernateCallback hcb = new HibernateCallback() {
            private String sql;
            private Object[] params;
            public HibernateCallback setQuery(String sql, Object[] params){
                this.sql = sql;
                this.params = params;
                return this;
            }

            @Override
            public List doInHibernate(Session sn) throws HibernateException, SQLException {
                Query q=sn.createSQLQuery(sql);
                if(params!=null){
                    for(int i=0; i<params.length;i++){
                        q.setParameter(i, params[i]);
                    }
                }
                    return q.list();
            }
        }.setQuery(query, params);

        return getHibernateTemplate().executeFind(hcb);
    }
    
    /**
     * executes the native SQL Update Query
     *
     * @param query the given query string
     * @param params the parameters to pass in the query
     * @return the list of records (rows)
     */
    public Object executeNativeUpdate(String query, Object[] params) {
        HibernateCallback hcb = new HibernateCallback() {
            private String sql;
            private Object[] params;
            public HibernateCallback setQuery(String sql, Object[] params){
                this.sql = sql;
                this.params = params;
                return this;
            }

            @Override
            public Object doInHibernate(Session sn) throws HibernateException, SQLException {
                Query q=sn.createSQLQuery(sql);
                if(params!=null){
                    for(int i=0; i<params.length;i++){
                        q.setParameter(i, params[i]);
                    }
                }
                return q.executeUpdate();
            }
        }.setQuery(query, params);

        return getHibernateTemplate().execute(hcb);
    }

    /**
     * Removes a entity from persistent storage permanently
     * @param entity the object to remove
     */
    public void delete(Object entity) {
        getHibernateTemplate().delete(entity);
    }
    
    /**
     * @param query
     * @param obj
     * @return
     */
    public List find(String query, Object[] obj){
        return getHibernateTemplate().find(query, obj);
    }

    public SqlRowSet queryForRowSetJDBC(String query, Object[] params){
        return jdbcTemplate.queryForRowSet(query, params);
    }

    public <T extends Object> List<T> queryJDBC(String sql, Object[] args, RowMapper<T> rowMapper) {
        return jdbcTemplate.query(sql, args, rowMapper);
    }

    public int updateJDBC(String sql, Object[] args) {
        return jdbcTemplate.update(sql, args);
    }

    public int queryForIntJDBC(String sql, Object[] args) {
        return jdbcTemplate.queryForInt(sql, args);
    }
    
    public Object executeNativeUpdate(String query) {
        return executeNativeUpdate(query, null);
    }

    public String buildQuery(String query,Clause[] clauses){
		if(clauses!=null){
			Arrays.sort(clauses);
			for(int i=0;i<clauses.length;i++){
				query+=clauses[i].getQueryString();
			}
		}

    	return query;
    }


}
