package jpabook.jpashop.repository.order.query;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.OrderStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.List;

@Data
/*
equals :  두 객체의 내용이 같은지, 동등성(equality) 를 비교하는 연산자
hashCode : 두 객체가 같은 객체인지, 동일성(identity) 를 비교하는 연산자

@EqualsAndHashCode(callSuper = true)로 설정시 부모 클래스 필드 값들도 동일한지 체크하며,
false(기본값)일 경우 자신 클래스의 필드 값만 고려

-> 중복 시, 어떤 기준으로 동일한지 확인하는 기준을 작성한다.
*/
@EqualsAndHashCode(of = "orderId")
public class OrderQueryDto {
    private Long orderId;
    private String name;
    private LocalDateTime orderDate;
    private OrderStatus orderStatus;
    private Address address;
    private List<OrderItemQueryDto> orderItems;

    public OrderQueryDto(Long orderId, String name, LocalDateTime orderDate, OrderStatus orderStatus, Address address) {
        this.orderId = orderId;
        this.name = name;
        this.orderDate = orderDate;
        this.orderStatus = orderStatus;
        this.address = address;
    }
    public OrderQueryDto(Long orderId, String name, LocalDateTime orderDate, OrderStatus orderStatus, Address address, List<OrderItemQueryDto> orderItems) {
        this.orderId = orderId;
        this.name = name;
        this.orderDate = orderDate;
        this.orderStatus = orderStatus;
        this.address = address;
        this.orderItems = orderItems;
    }
}
