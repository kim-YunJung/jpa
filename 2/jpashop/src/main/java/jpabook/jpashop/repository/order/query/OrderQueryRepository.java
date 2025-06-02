package jpabook.jpashop.repository.order.query;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@Repository
@RequiredArgsConstructor
public class OrderQueryRepository {
    private final EntityManager em;

    public List<OrderQueryDto> findOrderQueryDtos(){
        List<OrderQueryDto> result = findOrdes();

        result.forEach(o -> {
            List<OrderItemQueryDto> orderItems = findOrderItems(o.getOrderId());
            o.setOrderItems(orderItems);
        });

        return result;
    }

    private List<OrderItemQueryDto> findOrderItems(Long orderId) {
        return em.createQuery(
                "select new jpabook.jpashop.repository.order.query.OrderItemQueryDto(" +
                        "oi.order.id, i.name, oi.orderPrice, oi.count" +
                        ")" +
                        "from OrderItem oi" +
                        "join oi.item i" +
                        "where oi.order.id = :orderId"
                , OrderItemQueryDto.class
        )
                .setParameter("orderId", orderId)
                .getResultList();
    }

    public List<OrderQueryDto> findOrdes(){
        return em.createQuery(
                "select new jpabook.jpashop.repository.order.query.OrderQueryDto(" +
                        "o.id, m.name, o.orderDate, o.status, d.address" +
                        ")"+
                        "from Order o"+
                        "join o.member m"+
                        "join o.delivery d"
                , OrderQueryDto.class
        ).getResultList();
    }

    public List<OrderQueryDto> findAlBylDto_optimization() {
        List<OrderQueryDto> result = findOrdes();

        List<Long> orderIds = result.stream()
                .map(o -> o.getOrderId())
                .collect(Collectors.toList());

        List<OrderItemQueryDto> orderItems = em.createQuery(
                        "select new jpabook.jpashop.repository.order.query.OrderItemQueryDto(" +
                                "oi.order.id, i.name, oi.orderPrice, oi.count" +
                                ")" +
                                "from OrderItem oi" +
                                "join oi.item i" +
                                "where oi.order.id in :orderIds"
                        , OrderItemQueryDto.class
                )
                .setParameter("orderIds", orderIds)
                .getResultList();

        /* Map로 변경 */
       Map<Long, List<OrderItemQueryDto>> orderItemMap = orderItems.stream()
               //.collect(Collectors.groupingBy(orderItemQueryDto -> orderItemQueryDto.getOrderId()))
                .collect(Collectors.groupingBy(OrderItemQueryDto::getOrderId));

       //result에서 orderId와 동일한 값이 있는지 확인 후 값을 넣어준다.
        result.forEach(o -> o.setOrderItems(orderItemMap.get(o.getOrderId())));

        return result;
    }

    public List<OrderFlatDto> findAlBylDto_flat() {
        return em.createQuery("select new" +
                        "jpbook.jpashop.repository.order.query.OrderFlatDto(" +
                        "o.id, m.name, o.orderDate, o.status, d.address, i.name, oi.orderPrice, oi.count" +
                        ")" +
                        "from Order o" +
                        "join o.member m" +
                        "join o.delivery d" +
                        "join o.orderItems oi" +
                        "join oi.item i"
                , OrderFlatDto.class).getResultList();
    }
}
