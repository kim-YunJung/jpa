package jpabook.jpashop.repository.order.query;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

@Data
public class OrderItemQueryDto {
    /*
    객체의 직렬화는 객체의 내용을 바이트 단위로 변환하여
    파일 또는 네트워크를 통해서 스트림(송수신)이 가능하도록 하는 것을 의미

    @JsonIgnore 어노테이션은 클래스의 속성(필드, 멤버변수) 수준에서 사용
    @JsonIgnore 어노테이션이 붙은 필드는 직렬화에서 제외
    완전히 제외하려는 경우 접근자(종종 getter 메서드이지만 setter, 필드 또는 생성자 매개변수일 수 있음) 중 하나에 주석을 추가

    @JsonIgnoreProperties 어노테이션은 클래스 수준(클래스 선언 바로 위에)에 사용
    무시할 속성이나 속성 목록을 표시하는 데 사용

    @JsonIgnoreType 어노테이션은 클래스 수준에서 사용되며 전체 클래스를 무시
    주석이 달린 형식의 모든 속성을 무시하도록 지정하는 데 사용
    즉 클래스 자체를 JSON 데이터 맵핑에 사용불가
    */
    @JsonIgnore
    private Long orderId;
    private String itemName;
    private int orderPrice;
    private int count;

    public OrderItemQueryDto(Long orderId, String itemName, int orderPrice, int count) {
        this.orderId = orderId;
        this.itemName = itemName;
        this.orderPrice = orderPrice;
        this.count = count;
    }
}
