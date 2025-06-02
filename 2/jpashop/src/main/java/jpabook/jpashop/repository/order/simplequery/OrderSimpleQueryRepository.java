package jpabook.jpashop.repository.order.simplequery;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class OrderSimpleQueryRepository {
    private final EntityManager em;

    public List<SimpleOrderDto> findOrderDtos() {
        /* 해당 쿼리로는 객체만 반환된다 DTO반환을 위해서는 쿼리를 변경해야한다. */
        return em.createQuery(
                "select o from Order o"+
                        //내가 원하는 내용만 조회한다. 재사용이 불가하다.
                        //select new jpabook.jpashop.repository.SimpleOrderDto(o.id, m.name, o.orderdate, o.status, d.address)
                        "join o.member m"+
                        "join o.delivery d",
                SimpleOrderDto.class//반환 타입
        ).getResultList();
    }
}
