package jpabook.jpashop.repository.order.simplequery;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SimpleOrderDto{
    private Long orderId;
    private String name;
    private LocalDateTime orderDate;
    private OrderStatus orderStatus;
    private Address address;

    //쿼리 new문을 작성한경우 아래와 같이 작성해야한다.
    //public SimpleOrderDto(Long orderId, String name, LocalDateTime orderDate, OrderStatus orderStatus, Address address){
    public SimpleOrderDto(Order order){
        //쿼리 new문으로 작성한 경우
        //this.orderId = orderId;
        orderId = order.getId();
        name = order.getMember().getName();
        orderDate = order.getOrderDate();
        orderStatus = order.getStatus();
        address = order.getDelivery().getAddress();
    }
}