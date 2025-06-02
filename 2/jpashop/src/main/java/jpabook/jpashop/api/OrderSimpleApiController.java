package jpabook.jpashop.api;

import jpabook.jpashop.domain.Order;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import jpabook.jpashop.repository.order.simplequery.OrderSimpleQueryRepository;
import jpabook.jpashop.repository.order.simplequery.SimpleOrderDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import static java.util.stream.Collectors.toList;

/* *
* xToOne(ManyToOne, OneToOne)
* Order
* Order -> Member
* Order -> Delivery
* */
@RestController
@RequiredArgsConstructor
public class OrderSimpleApiController {
    private final OrderRepository orderRepository;
    private final OrderSimpleQueryRepository orderSimpleQueryRepository;

    /* ordersV1 / ordersV2 - N+1의 문제가 발생한다. */
    @GetMapping("/api/v1/simple-orders")
    public List<Order> ordersV1() {
        List<Order> all = orderRepository.findAll(new OrderSearch());
        /* 필요한 내용만 호출 */
        for(Order order : all){
            /* 강제로 레이즈 로딩 */
            /*  order.getMember() 해당 부분 까진 prox객체이다.
            * getName() 호출 시, db에 쿼리가 실행되어 강제로 데이터를 조회하게 한다.
            * -> Lazy 강제 초기화 */
            order.getMember().getName();
            order.getDelivery().getAddress();
        }
        return all;
    }

    @GetMapping("/api/v2/simple-orders")
    public List<SimpleOrderDto> ordersV2() {
        List<Order> orders = orderRepository.findAll(new OrderSearch());

        return orders.stream()
                //.map(order -> new SimpleOrderDto(order))
                .map(SimpleOrderDto::new)
                //.collect(Collectors.toList());
                .collect(toList());
    }

    @GetMapping("/api/v3/simple-orders")
    public List<SimpleOrderDto> ordersV3() {
        List<Order> orders = orderRepository.findAllWithMemberDelivery();
        return orders.stream()
                .map(order -> new SimpleOrderDto(order))
                .collect(toList());
    }

    @GetMapping("/api/v4/simple-orders")
    public List<SimpleOrderDto> ordersV4() {
        return orderSimpleQueryRepository.findOrderDtos();
    }
}
