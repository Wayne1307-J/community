package indi.nowcoder.community.controller.advice;
import indi.nowcoder.community.util.CommunityUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.io.IOException;
import java.io.PrintWriter;
@Slf4j
@ControllerAdvice(annotations = Controller.class)
public class ExceptionAdvice {
    @ExceptionHandler({Exception.class})
    public void handleException(Exception e, HttpServletRequest request, HttpServletResponse response) throws IOException {
        log.error("服务器发生异常: " + e.getMessage()); // 记录日志
        for (StackTraceElement element : e.getStackTrace()) {
            log.error(element.toString()); //记录日志详细信息
        }
        // 判断是普通请求还是异步请求，通过request进行判断
        String xRequestedWith = request.getHeader("x-requested-with");
        if ("XMLHttpRequest".equals(xRequestedWith)) { // 异步请求
            response.setContentType("application/plain;charset=utf-8");
            PrintWriter writer = response.getWriter();  // 获取一个输出流
            writer.write(CommunityUtils.getJSONString(1, "服务器异常!"));
        } else { // 普通请求是重定向
            response.sendRedirect(request.getContextPath() + "/error");
        }
    }

}
