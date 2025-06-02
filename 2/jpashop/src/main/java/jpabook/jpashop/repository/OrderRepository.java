package jpabook.jpashop.repository;

import jakarta.persistence.EntityManager;
import jpabook.jpashop.domain.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class OrderRepository {
    private final EntityManager em;

    public void save(Order order){
        em.persist(order);
    }

    public Order findOne(Long id){
        return em.find(Order.class, id);
    }

    public List<Order> findAll(OrderSearch orederSearch){
        /*
        List<Order> resultList = em.createQuery("SELEC O FROM ORDER O JOIN O.MEMBER M" +
                        "WHERE O.STATUS = :STATUS" +
                        "AND M.NAME LIKE :NAME", Order.class)
                .setParameter("STATUS", orederSearch.getOrderStatus())
                .setParameter("NAME", orederSearch.getMemberName())
                .setMaxResults(1000)//최대 천건을 조회한다.
                .getResultList();
        */

        /* JPA Criteria */

        /* Querydsl */

        return null;
    }

    /* N+1문제를 해결하기 위해 join fetch를 사용한다. */
    public List<Order> findAllWithMemberDelivery(){
        //재사용이 가능하다.
        return em.createQuery(
                "select o from Order o"+
                    "join fetch o.member m"+
                    "join fetch o.delivery d",
                    Order.class
        ).getResultList();
    }

    /* N+1문제를 해결하기 위해 join fetch를 사용한다.
    * 페이징 처리를 위해 offset, limit를 파라미터로 받는다. */
    public List<Order> findAllWithMemberDelivery(int offsert, int liimt){
        //재사용이 가능하다.
        return em.createQuery(
                "select o from Order o"+
                        "join fetch o.member m"+
                        "join fetch o.delivery d",
                Order.class
        ).setFirstResult(offsert)
                .setMaxResults(liimt)
                .getResultList();
    }

    public List<Order> findAllWithItem() {
        /* 현재는 데이터가 중복으로 나온다.
        return em.createQuery(
                "select o from Order o"+
                   "fetch join o.member m"+
                   "fetch join o.delivery d"+
                   "fetch join o.orderItems oi"+
                   "fetch join oi.item i"
                , Order.class
        ).getResultList();
        */
        /* 중복제거 - db에서는 모든 데이터가 동일해야 중복되나,
        jpa distinct는 아이디가 동일하면 중복을 제거한다. */
        return em.createQuery(
                "select distinct o from Order o"+
                        "fetch join o.member m"+
                        "fetch join o.delivery d"+
                        "fetch join o.orderItems oi"+
                        "fetch join oi.item i"
                , Order.class
        ).getResultList();
    }
}
