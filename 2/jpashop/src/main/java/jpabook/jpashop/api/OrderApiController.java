package jpabook.jpashop.api;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderItem;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import jpabook.jpashop.repository.order.query.OrderFlatDto;
import jpabook.jpashop.repository.order.query.OrderItemQueryDto;
import jpabook.jpashop.repository.order.query.OrderQueryDto;
import jpabook.jpashop.repository.order.query.OrderQueryRepository;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

import static java.util.stream.Collectors.*;

@RestController
@RequiredArgsConstructor
public class OrderApiController {
    private final OrderRepository orderRepository;
    private final OrderQueryRepository orderQueryRepository;

    /**
     * V1. 엔티티 직접 노출
     * - 엔티티가 변하면 API 스펙이 변한다.
     * - 트랜잭션 안에서 지연 로딩 필요
     * - 양방향 연관관계 문제
     *
     * V2. 엔티티를 조회해서 DTO로 변환(fetch join 사용X)
     * - 트랜잭션 안에서 지연 로딩 필요
     * V3. 엔티티를 조회해서 DTO로 변환(fetch join 사용O)
     * - 페이징 시에는 N 부분을 포기해야함(대신에 batch fetch size? 옵션 주면 N -> 1 쿼리로 변경 가
     능)
     *
     * V4. JPA에서 DTO로 바로 조회, 컬렉션 N 조회 (1 + N Query)
     * - 페이징 가능
     * V5. JPA에서 DTO로 바로 조회, 컬렉션 1 조회 최적화 버전 (1 + 1 Query)
     * - 페이징 가능
     * V6. JPA에서 DTO로 바로 조회, 플랫 데이터(1Query) (1 Query)
     * - 페이징 불가능...
     *
     */
    /* 객체를 직접 노출하기 때문에 다른 방법을 사용해야한다. */
    @GetMapping("/api/v1/orders")
    public List<Order> ordersv1(){
        List<Order> all = orderRepository.findAll(new OrderSearch());
        for(Order order :all){
            order.getMember().getName();
            order.getDelivery().getAddress();

            List<OrderItem> orderItems = order.getOrderItems();
            orderItems.stream().forEach(o->o.getItem().getName());
        }
        return all;
    }

    /* 컬렉션을 DTO로 변경 */
    @GetMapping("/api/v2/orders")
    public List<OrderDto> ordersv2(){
        List<Order> orders = orderRepository.findAll(new OrderSearch());
        return orders.stream()
                .map(o -> new OrderDto(o))
                .collect(toList());
    }

    //@Getter
    @Data
    static class OrderDto{
        private Long orderId;
        private String name;
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        private Address address;
        //해당 부분도 Dto로 변경해야한다.
        //private List<OrderItem> orderItems;
        private List<OrderItemDto> orderItems;

        private OrderDto(Order o){
            orderId = o.getId();
            name = o.getMember().getName();
            orderDate = o.getOrderDate();
            orderStatus = o.getStatus();
            address = o.getDelivery().getAddress();

            //orderItems = o.getOrderItems();
            /*
            o.getOrderItems().stream().forEach(i -> i.getItem().getName());
            orderItems = o.getOrderItems();
            */
            orderItems = o.getOrderItems().stream()
                    //.map(orderItem -> new OrderItemDto(orderItem))
                    .map(OrderItemDto::new)
                    //.collect(Collectors.toList());
                    .collect(toList());
        }
    }

    @Getter
    static class OrderItemDto{
        private String itemName;
        private int orderPrice;
        private int count;

        private OrderItemDto(OrderItem o){
            itemName = o.getItem().getName();
            orderPrice = o.getOrderPrice();
            count = o.getCount();
        }
    }

    /* fetch join */
    @GetMapping("/api/v3/orders")
    public List<OrderDto> ordersv3(
            @RequestParam(value = "offset", defaultValue = "0") int offset
            , @RequestParam(value = "limit", defaultValue = "100") int limit
            ){
        /* 1:N을 fetch join하면 페이징처리가 되지 않는다. */
        /*List<Order> orders = orderRepository.findAllWithItem();*/

        /* 페이징 처리 방법
        - 페이징 + 컬렉션 엔티티 함께 조회
           -> ToOne (OneToOne, ManyToOne)관걔를 모두 패치조인
           ToOne 관계는 row수를 증가 시키지 않아, 페이징 쿼리에 영향을 주지 않는다.
           -> 컬렉션은 지연 로딩
           (hidernate.default_batch_fetch_size, @BetchSize)
        */
        List<Order> orders = orderRepository.findAllWithMemberDelivery(offset, limit);

        return orders.stream()
                .map(OrderDto::new)
                .collect(toList());
    }

    @GetMapping("/api/v4/orders")
    public List<OrderQueryDto> ordersv4(){
        return orderQueryRepository.findOrderQueryDtos();
    }

    @GetMapping("/api/v5/orders")
    public List<OrderQueryDto> ordersv5(){
        return orderQueryRepository.findAlBylDto_optimization();
    }

    @GetMapping("/api/v6/orders")
    public List<OrderQueryDto> ordersv6(){
        List<OrderFlatDto> flats = orderQueryRepository.findAlBylDto_flat();

        return flats.stream()
                .collect(groupingBy(o -> new OrderQueryDto(o.getOrderId(), o.getName(), o.getOrderDate(), o.getOrderStatus(), o.getAddress()),
                        mapping(o -> new OrderItemQueryDto(o.getOrderId(), o.getItemName(), o.getOrderPrice(), o.getCount()), toList())
                )).entrySet().stream()
                .map(e -> new OrderQueryDto(e.getKey().getOrderId(), e.getKey().getName(), e.getKey().getOrderDate(), e.getKey().getOrderStatus(), e.getKey().getAddress(), e.getValue()))
                .collect(toList());
    }
}
