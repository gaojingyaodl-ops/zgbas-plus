package com.spt.tools.jpa.persistence;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

public class DynamicSpecifications {
	public static <T> Specification<T> bySearchFilter(final Collection<SearchFilter> filters) {
		return new Specification<T>() {
			@Override
			public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
				if (CollectionUtils.isNotEmpty(filters)) {

					List<Predicate> predicates = new ArrayList<>();
					for (SearchFilter filter : filters) {
						Predicate predicate = parseFilter(filter, builder, root);
						for(SearchFilter filterOr : filter.getLstOr()){
							Predicate predicateOr =  parseFilter(filterOr, builder, root);
							predicate= builder.or(predicate,predicateOr);
						}
						predicates.add(predicate);
					}

					// 将所有条件用 and 联合起来
					if (predicates.size() > 0) {
						return builder.and(predicates.toArray(new Predicate[predicates.size()]));
					}
				}

				return builder.conjunction();
			}
		};
	}

	
	private static <T> Predicate parseFilter(SearchFilter filter,CriteriaBuilder builder,Root<T> root){
		// nested path translate, 如Task的名为"user.name"的filedName,
		// 转换为Task.user.name属性
		String[] names = StringUtils.split(filter.fieldName, ".");
		Path expression = root.get(names[0]);
		if (names.length==2){
			Join join = root.join(names[0]);
			expression = join.get(names[1]);
		}
//		for (int i = 1; i < names.length; i++) {
//			expression = expression.get(names[i]);
//		}

		Predicate predicate = null;
		// logic operator
		switch (filter.operator) {
		case EQ:
			if (expression.getJavaType().isEnum()){
				for (Object obj :expression.getJavaType().getEnumConstants()){
					if (obj.toString().equals(filter.value)){
						predicate = builder.equal(expression, obj);
						break;
					}
				}
			}else{
				predicate = builder.equal(expression, filter.value);
			}
			break;
		case NEQ:
			if (expression.getJavaType().isEnum()){
				for (Object obj :expression.getJavaType().getEnumConstants()){
					if (obj.toString().equals(filter.value)){
						predicate = builder.notEqual(expression, obj);
						break;
					}
				}
			}else{
				predicate = builder.notEqual(expression, filter.value);
			}
			break;
		case NN:
			predicate = builder.isNotNull(expression);
			break;
		case ISNULL:
			predicate = builder.isNull(expression);
			break;
		case LIKES:
			predicate =builder.like(expression, filter.value + "%");
			break;
		case LIKEE:
			predicate = builder.like(expression,  "%" + filter.value);
			break;
		case LIKE:
			predicate = builder.like(expression, "%" + filter.value + "%");
			break;
		case GT:
			predicate = builder.greaterThan(expression, (Comparable) filter.value);
			break;
		case LT:
			predicate = builder.lessThan(expression, (Comparable) filter.value);
			break;
		case GTE:
			predicate = builder.greaterThanOrEqualTo(expression, (Comparable) filter.value);
			break;
		case LTE:
			predicate = builder.lessThanOrEqualTo(expression, (Comparable) filter.value);
			break;
		case BT:
			Expression<Date> date = (Expression<Date>)expression;
			FromToValue<Date> ft =(FromToValue<Date>)filter.value;
			predicate = builder.between(date, (Date)ft.from, (Date)ft.to);
			break;
		case IN:
			
			if (filter.value instanceof List){
				List list=(List)filter.value;
				predicate = expression.in(list);
			}else{
				predicate = expression.in(filter.value);
			}
			break;
		case NIN:
			if (filter.value instanceof List){
				List list=(List)filter.value;
				predicate = expression.in(list);
			}else{
				predicate = expression.in(filter.value);
			}
			predicate = builder.not(predicate);
			break;
		}
		return predicate;
	}
	
//	public static <T> Specification<T> onePredicate(SearchFilter filter) {
//		// nested path translate, 如Task的名为"user.name"的filedName,
//		// 转换为Task.user.name属性
//		String[] names = StringUtils.split(filter.fieldName, ".");
//		Path expression = root.get(names[0]);
//		for (int i = 1; i < names.length; i++) {
//			expression = expression.get(names[i]);
//		}
//
//		// logic operator
//		switch (filter.operator) {
//		case EQ:
//			predicates.add(builder.equal(expression, filter.value));
//			break;
//		case LIKE:
//			predicates.add(builder.like(expression, "%" + filter.value + "%"));
//			break;
//		case GT:
//			predicates.add(builder.greaterThan(expression, (Comparable) filter.value));
//			break;
//		case LT:
//			predicates.add(builder.lessThan(expression, (Comparable) filter.value));
//			break;
//		case GTE:
//			predicates.add(builder.greaterThanOrEqualTo(expression, (Comparable) filter.value));
//			break;
//		case LTE:
//			predicates.add(builder.lessThanOrEqualTo(expression, (Comparable) filter.value));
//			break;
//		case IN:
//			List vals = (List) filter.value;
//			predicates.add(expression.in(vals));
//			break;
//		}
//	}
}
