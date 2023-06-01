package cl.duoc.portafolio.feriavirtual.repository.dao;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import cl.duoc.portafolio.feriavirtual.domain.Cosecha;
import cl.duoc.portafolio.feriavirtual.repository.ICosechaDAO;
import cl.duoc.portafolio.feriavirtual.util.SearchCriteria;

@Repository
public class CosechaDAO implements ICosechaDAO{

	@PersistenceContext
	private EntityManager entityManager;

	@Override
	public List<Cosecha> search(List<SearchCriteria> params, Pageable pageable) {
		final CriteriaBuilder builder = entityManager.getCriteriaBuilder();
		final CriteriaQuery<Cosecha> query = builder.createQuery(Cosecha.class);
		final Root<Cosecha> root = query.from(Cosecha.class);

		Predicate predicate = builder.conjunction();
		List<Order> orderList = new ArrayList<>();

		for (SearchCriteria param : params) {

			switch (param.getOperation()) {
			case greaterThanOrEqualTo:
				predicate = builder.and(predicate,
						builder.greaterThanOrEqualTo(root.get(param.getKey()), param.getValue().toString()));
				break;
			case lessThanOrEqualTo:
				predicate = builder.and(predicate,
						builder.lessThanOrEqualTo(root.get(param.getKey()), param.getValue().toString()));
				break;
			case like:
				if (param.getKey2() == null) {
					if (root.get(param.getKey()).getJavaType() == String.class) {
						predicate = builder.and(predicate,
								builder.like(root.get(param.getKey()), "%" + param.getValue() + "%"));
					} else {
						predicate = builder.and(predicate, builder.equal(root.get(param.getKey()), param.getValue()));
					}
				} else {
					predicate = builder.and(predicate,
							builder.or(builder.like(root.get(param.getKey()), "%" + param.getValue() + "%"),
									builder.like(root.get(param.getKey2()), "%" + param.getValue() + "%")));
				}
				break;
			case between:
				predicate = builder.and(predicate, builder.between(root.get(param.getKey()),
						(LocalDate) param.getValue(), (LocalDate) param.getValue2()));
				break;
			case equal:
				predicate = builder.and(predicate, builder.equal(root.get(param.getKey()), param.getValue()));
				break;
			case in:
				break;
			case exist:
				break;
			}
		}
		orderList.add(builder.asc(root.get("id")));
		query.where(predicate).orderBy(orderList);

		if (pageable != null)
			return entityManager.createQuery(query.select(root)).setFirstResult(pageable.getPageNumber())
					.setMaxResults(pageable.getPageSize()).getResultList();
		else
			return entityManager.createQuery(query.select(root)).getResultList();
	}
}
