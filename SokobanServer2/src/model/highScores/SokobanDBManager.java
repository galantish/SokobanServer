package model.highScores;

import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;
import javax.persistence.Query;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import db.Level;
import db.QueryParameters;
import db.Record;

public class SokobanDBManager 
{
	private static class SokobanDBManagerHolder
	{
		public static final SokobanDBManager instance = new SokobanDBManager();
	}
	
	private SessionFactory factory;

	public static SokobanDBManager getInstance() 
	{
		return SokobanDBManagerHolder.instance;
	}

	private SokobanDBManager() 
	{
		// to show the severe message
		Logger.getLogger("org.hibernate").setLevel(java.util.logging.Level.SEVERE);

		// reading the XML so he can connect to the DB
		Configuration configuration = new Configuration();
		configuration.configure();
		factory = configuration.buildSessionFactory();
	}
	
	public List<Record> recordsQuery(QueryParameters params)
	{
		Session session = null;
		Record record=null;
		Query query=null;
		List<Record>list = null;
		
		try 
		{
			session = factory.openSession();
			if(params.getLevelId().equals("") && params.getUserName().equals(""))
			{
				query = session.createQuery("FROM Records as rec ORDER BY rec." + params.getOrderBy());
			}
			
			else if(!(params.getUserName().equals(""))&&!(params.getLevelId().equals("")))
			{
				query=session.createQuery("FROM Records as rec WHERE rec.userName=:userName AND rec.levelID = :levelID ORDER BY rec." + params.getOrderBy());
				query.setParameter("userName", params.getUserName());
				query.setParameter("levelID", params.getLevelId());
			}
			
			else if(!params.getLevelId().equals(""))
			{
				query = session.createQuery("FROM Records as rec WHERE rec.levelID = :levelID ORDER BY rec." + params.getOrderBy());
				query.setParameter("levelID", params.getLevelId()); 
			}
			
			else if(!params.getUserName().equals(""))
			{
				query=session.createQuery("FROM Records as rec where rec.userName = :userName ORDER BY rec." + params.getOrderBy());
				query.setParameter("userName", params.getUserName());
			}
			
			query.setMaxResults(16);
			list = query.getResultList();
			Iterator<Record>it=list.iterator();
			
			while(it.hasNext())
			{
				record=it.next();
				System.out.println(record);
			}
		
		} 
		catch (HibernateException ex) 
		{
			System.out.println(ex.getMessage());
		} 
		finally 
		{
			if (session != null)
				session.close();
		}
		
		return list;
	}
	
	public void getAllRecords()
	{
		Session session = null;
		Record record;
		try 
		{
			session = factory.openSession();
			Query query=session.createQuery("FROM Records");
			List<Record>list=query.getResultList();
			Iterator<Record>it=list.iterator();
			
			while(it.hasNext())
			{
				record=it.next();
				System.out.println(record);
			}
		} 
		catch (HibernateException ex) 
		{
			System.out.println(ex.getMessage());
		} 
		finally 
		{
			if (session != null)
				session.close();
		}
	}
	
	public boolean isExistLevel(String levelID)
	{
		Session session = null;
		Record record;
		try 
		{
			session = factory.openSession();
			Query query=session.createQuery("FROM Levels as l WHERE l.levelID = :levelID");
			query.setParameter("levelID", levelID);
			List<Level>list = query.getResultList();
			
			if(list.size() > 0)
				return true;
		} 
		catch (HibernateException ex) 
		{
			System.out.println(ex.getMessage());
		} 
		finally 
		{
			if (session != null)
				session.close();
		}
		return false;
	}
	
	public boolean isExistUser(String userName)
	{
		Session session = null;
		Record record;
		try 
		{
			session = factory.openSession();
			Query query=session.createQuery("FROM Users as u WHERE u.name = :name");
			query.setParameter("name", userName);
			List<Level>list = query.getResultList();
			
			if(list.size() > 0)
				return true;
		} 
		catch (HibernateException ex) 
		{
			System.out.println(ex.getMessage());
		} 
		finally 
		{
			if (session != null)
				session.close();
		}
		return false;
	}
	
	public void add(Object obj)
	{
		Session session = null;
		Transaction tx = null;

		try 
		{
			session = factory.openSession();
			tx = session.beginTransaction();

			session.save(obj);
			tx.commit();
		} 
		catch (HibernateException ex) 
		{
			if (tx != null)
				tx.rollback();
			System.out.println(ex.getMessage());
		} 
		finally 
		{
			if (session != null)
				session.close();
		}
	}
	
	public void close() 
	{
		factory.close();
	}
}