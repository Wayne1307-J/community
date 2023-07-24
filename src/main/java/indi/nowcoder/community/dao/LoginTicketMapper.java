package indi.nowcoder.community.dao;

import indi.nowcoder.community.entity.LoginTicket;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.annotations.Param;

@Mapper
@Deprecated
public interface LoginTicketMapper {
    @Insert("insert into login_ticket(user_id,ticket,status,expired)"+"values(#{userId},#{ticket},#{status},#{expired})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertLoginTicket(LoginTicket loginTicket);

    @Select("select id,user_id,ticket,status,expired from login_ticket where ticket=#{ticket}")
    LoginTicket selectByTicket(String ticket);

    //写法一
//    @Update(
//            "<script>"+
//            "update login_ticket set status=#{status}"+
//                "<where>" +
//                    "<if test=\"ticket!=null\">" +
//                        "ticket=#{ticket}" +
//                    "</if>" +
//                "</where>" +
//            "</script>"
//    )
    @Update({
            "<script>",
            "update login_ticket set status=#{status} where ticket=#{ticket} ",
            "<if test=\"ticket!=null\"> ",
            "and 1=1 ",
            "</if>",
            "</script>"
    })
    int updateStatus(String ticket, int status);


}
