package com.negeso.module.webshop.dao;

import com.negeso.framework.dao.impl.AbstractHibernateDao;
import com.negeso.module.webshop.entity.Product;
import org.hibernate.FetchMode;
import org.hibernate.Query;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.proxy.HibernateProxy;
import org.hibernate.transform.Transformers;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public class ProductDaoImpl extends AbstractHibernateDao<Product, String> implements ProductDao{
	public ProductDaoImpl() {
		super(Product.class);
	}


	@Override
	@SuppressWarnings("unchecked")
	public Product findOneJoinDiscount(String id) {
		Product product = (Product) super.createCriteria()
				.setFetchMode("priceListProduct", FetchMode.JOIN)
				.setFetchMode("articleGroup", FetchMode.JOIN)
				.add(Restrictions.eq("id", id))
				.uniqueResult();

		if (product instanceof HibernateProxy) {
			product = (Product) ((HibernateProxy) product).getHibernateLazyInitializer().getImplementation();
		}
		return product;
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<Product> listByEANCodes(String[] codes) {
		return (List<Product>) super.createCriteria()
				.add(Restrictions.in("ean", codes))
				.list();
	}

	@Override
	public Product findOneByEAN(String code) {
		return (Product) super.createCriteria()
				.add(Restrictions.eq("ean", code))
				.add(Restrictions.eq("visible", true))
				.uniqueResult();
	}

	@Override
	public List<Product> listSales() {
		return (List<Product>) super.createCriteria()
				.add(Restrictions.eq("sale", true))
				.list();
	}

	@Override
	public void updateIgnorableFields(Product product) {
		Query query = super.getCurrentSession().createQuery("update Product set sale = :sale where id = :id");
		query.setParameter("sale", product.getSale());
		query.setParameter("id", product.getId());
		query.executeUpdate();
	}

	@Override
	public List<String> listIds() {
		return (List<String>) super.getCurrentSession().createQuery("SELECT c.productNumber FROM Product c").list();
	}

	@Override
	@Transactional
	public List<Product> listProductIds() {
//		return (List<Product>) super.getCurrentSession().createQuery("SELECT p.productNumber, p.id, p.title FROM Product p").list();
		return (List<Product>)super.createCriteria()
				.setProjection(
						Projections.projectionList()
						.add(Projections.property("id"), "id")
						.add(Projections.property("productNumber"), "productNumber")
						.add(Projections.property("title"), "title")
				)
				.setResultTransformer(Transformers.aliasToBean(Product.class))
				.list()
				;
	}
}
