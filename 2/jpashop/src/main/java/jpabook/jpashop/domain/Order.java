package jpabook.jpashop.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.BatchSize;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Setter @Getter
@ToString(callSuper = true, exclude = "orderItems")
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@AllArgsConstructor(staticName = "of")
@Table(name="ordes")
public class Order extends AuditingFields{
    @Id
    @GeneratedValue
    @Column(name = "order_id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;/* 연관관계의 주인으로 기준점 */
    /*
    * LAZY로 설정할 경우 Order객체만 가져오고 Member객체는 가지고 오지 않는다.
    * 임의로 new ProxMember();를 상속받아 해당 객체를 넣어놓는다.
    *
    * 프록시 객체(Proxy Object)란, 특정 객체에 대한 대리자 또는 중간자 역할을 하는 객체를 말합니다.
    * 이 객체는 실제 객체(원래 객체)에 대한 접근을 제어하거나, 추가적인 로직(예: 로깅, 성능 모니터링, 캐싱)을 삽입하는 데 사용됩니다.
    * 1. 프록시 객체란?
프록시는 디자인 패턴 중 하나인 프록시 패턴(Proxy Pattern)에서 유래된 개념입니다. 이것은 "어떤 객체에 직접 접근하는 대신 대리인(proxy)을 통해 접근"하는 구조를 만들기 위한 것입니다.

프록시 객체는 실제 객체를 감싸고(control or wrap) 있지만, 클라이언트는 실제 객체인지, 프록시 객체인지 구분하지 못하도록 같은 인터페이스를 구현합니다.
이를 통해, 프록시 객체가 추가적인 기능을 수행하면서도 원본 객체처럼 동작할 수 있습니다.

---

2. 프록시 객체의 용도
프록시는 다양한 용도로 사용되며, 주로 아래의 목적으로 활용됩니다:

1. 접근 제어
   - 특정 객체에 대한 접근을 제한하거나 권한을 검사할 때 사용됩니다.
     예: 사용자가 데이터를 조회하기 전에 인증 여부를 확인.

2. 지연 로딩(Lazy Initialization)
   - 실제 객체를 바로 생성하지 않고, 필요한 시점에 생성하도록 지연시킵니다.
     예: 데이터베이스에서 큰 데이터를 로딩하기 전에 프록시를 통해 지연 로딩.

3. 추가 기능 삽입(Decorating)
   - 기존 객체의 기능에 새로운 기능(예: 로깅, 캐싱)을 추가.
     예: 메서드 호출 시마다 로그를 기록하거나 결과를 캐싱.

4. 원격 호출(Remote Proxy)
   - 원격지에 있는 객체를 로컬에서 사용하듯 제공.
     예: 원격 서버의 메서드를 호출할 때, 프록시를 통해 호출.

5. 보호(Protection Proxy)
   - 민감한 리소스(예: 파일, 네트워크)에 대한 권한 확인 뒤 사용.

---

3. 자바에서의 프록시 객체
Java에서는 `java.lang.reflect.Proxy`를 사용하여 동적 프록시(Dynamic Proxy)를 생성할 수 있습니다. 동적 프록시는 런타임에 인터페이스를 기반으로 프록시 객체를 생성합니다.

#프록시 객체의 생성 과정
1. 클라이언트 → 프록시 객체: 클라이언트는 원본 객체 대신 프록시 객체를 호출.
2. 프록시 객체 → 내부 작업 처리: 프록시 객체는 먼저 추가 로직(예: 로깅, 인증)을 처리.
3. 프록시 객체 → 실제 객체: 작업이 끝난 후 실제 객체에 요청 전달.
    * */

    /*
    Cascade는 엔티티 간 관계가 명확할 때 사용

    특정 엔티티를 영속상태로 만들 때 연관관계에 있는 엔티티도 함께 영속상태로 만들기 위해서 사용한다.
    부모 엔티티를 다룰때 연관 되어있는 자식 엔티티까지 다룰수 있게 해준다.

    CascadeType.ALL - 모든 Cascade를 적용한다.
    CascadeType.PERSIST - 엔티티를 영속화할 때, 연관된 하위 엔티티도 함께 유지한다.
    CascadeType.MERGE - 엔티티 상태를 병합(Merge)할 때, 연관된 하위 엔티티도 모두 병합한다.
    CascadeType.REMOVE - 엔티티를 제거할 때, 연관된 하위 엔티티도 모두 제거한다.
    CascadeType.DETACH - 영속성 컨텍스트에서 엔티티 제거
    부모 엔티티를 detach() 수행하면, 연관 하위 엔티티도 detach()상태가 되어 변경 사항을 반영하지 않는다.
    CascadeType.REFRESH - 상위 엔티티를 새로고침(Refresh)할 때, 연관된 하위 엔티티도 모두 새로고침한다.
    */
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    @OrderBy("createdAt DESC")
    //@BatchSize(size = 100)
    /* application.yml = 모든 부분에 적용,
    원하는 부분에 적용을 원할 경우 @BatchSize를 사용한다.
    컬렉션일 경우 위와 같이 적용한다. 컬렉션이 아닐 경우 @Entity의 상단에 작성한다.

    사이즈는 100 ~1000사이가 적당하다. 1000개 이상일 경우 부하 증가
     */
    private List<OrderItem> orderItems = new ArrayList<>();

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "delivery_id")
    /* FK 접속이 많은 곳에 설정한다. */
    private Delivery delivery;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;//주문상태

    private LocalDateTime orderDate;

    /* === 연관관계 메서드 === */
    public void setMember(Member member){
        this.member = member;
        member.getOrders().add(this);
    }

    public void addOrderItem(OrderItem orderItem){
        orderItems.add(orderItem);
        orderItem.setOrder(this);
    }

    public void setDelivery(Delivery delivery){
        this.delivery = delivery;
        delivery.setOrder(this);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return Objects.equals(id, order.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    /* 생성 메서드 */
    public static Order createOrder(Member member, Delivery delivery, OrderItem ... orderItems){
        Order order = new Order();
        order.setMember(member);
        order.setDelivery(delivery);
        for (OrderItem orderItem : orderItems) {
            order.addOrderItem(orderItem);
        }
        order.setStatus(OrderStatus.ORDER);
        order.setOrderDate(LocalDateTime.now());

        return order;
    }

    /* 비즈니스 로직 */
        /* 주문 취소 */
    public void cancel(){
        if(delivery.getStatus() == DeliveryStatus.COMP){
            throw new IllegalStateException("이미 배송완료된 상품은 취소가 불가능합니다.");
        }
        /* 상태를 주문 취소로 변경 */
        this.setStatus(OrderStatus.CANCEL);

        /* 제고 수량 복구 */
        for (OrderItem orderItem : orderItems) {
            orderItem.cancel();
        }
    }

    /* 조회 로직 */
    /**
     * 전체 주문 가격 조회
     */
    public int getTotalPrice(){
        return orderItems.stream().mapToInt(OrderItem::getTotalPrice).sum();
        /*
        int totalPrice = 0;
        for (OrderItem orderItem : orderItems){
            totalPrice+= orderItem.getTotalPrice();
        }
        return totalPrice;
        */
    }
}
