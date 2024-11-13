package com.cowave.commons.client.http.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.cowave.commons.client.http.constants.HttpCode.INTERNAL_SERVER_ERROR;
import static com.cowave.commons.client.http.constants.HttpCode.SUCCESS;

/**
 *
 * @author shanhuiming
 *
 */
@Slf4j
@Getter
@Setter
public class Response<T> {

    /**
     * 响应数据
     */
    private T data;

    /**
     * 响应码
     */
    private String code;

    /**
     * 响应描述
     */
    private String msg;

    /**
     * 错误堆栈信息
     */
    private List<String> cause;

    public Response() {

    }

    public Response(String code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    @Override
    public String toString() {
        return "{code=" + code + ", msg=" + msg + ", data=" + data + "}";
    }

    /**
     * status=200, code=#{responseCode.code}, msg=#{responseCode.msg}, data=null
     */
    public static <V> Response<V> code(ResponseCode responseCode) {
        return new Response<>(responseCode.getCode(), responseCode.getMsg(), null);
    }

    /**
     * status=200, code=#{responseCode.code}, msg=#{responseCode.msg}, data=#{data}
     */
    public static <V> Response<V> data(ResponseCode responseCode, V data) {
        return new Response<>(responseCode.getCode(), responseCode.getMsg(), data);
    }

    /**
     * status=200, code=#{resp.code}, msg=#{msg}, data=null
     */
    public static <V> Response<V> msg(ResponseCode responseCode, String msg) {
        return new Response<>(responseCode.getCode(), msg, null);
    }

    /**
     * status=200, code=200, msg="success", data=null
     */
    public static <V> Response<V> success() {
        return new Response<>(SUCCESS.getCode(), SUCCESS.getMsg(), null);
    }

    /**
     * status=200, code=200, msg="success", data=null
     */
    public static <V> Response<V> success(Action action) throws Exception {
        if (action != null) {
            action.exec();
        }
        return success();
    }

    /**
     * status=200, code=200, msg="success", data=#{data}
     */
    public static <V> Response<V> success(V data) {
        return new Response<>(SUCCESS.getCode(), SUCCESS.getMsg(), data);
    }

    /**
     * status=200, code=200, msg=#{msg}, data=#{data}
     */
    public static <V> Response<V> success(V data, String msg) {
        return new Response<>(SUCCESS.getCode(), msg, data);
    }

    /**
     * status=200, code=500, msg="Internal Server Error", data=null
     */
    public static <V> Response<V> error() {
        return new Response<>(INTERNAL_SERVER_ERROR.getCode(), INTERNAL_SERVER_ERROR.getMsg(), null);
    }

    /**
     * status=200, code=500, msg=#{msg}, data=null
     */
    public static <V> Response<V> error(String msg) {
        return new Response<>(INTERNAL_SERVER_ERROR.getCode(), msg, null);
    }

    /**
     * status=200, code=200, msg="success", data=#{page}
     */
    public static <E> Response<Page<E>> page(List<E> list) {
        Response<Page<E>> response = new Response<>(SUCCESS.getCode(), SUCCESS.getMsg(), null);
        if (list == null) {
            list = new ArrayList<>();
        }

        if (list instanceof com.github.pagehelper.Page) {
            com.github.pagehelper.Page<E> page = (com.github.pagehelper.Page<E>) list;
            response.setData(new Page<>(page, page.getTotal()));
        } else {
            response.setData(new Page<>(list, list.size()));
        }
        return response;
    }

    /**
     * status=200, code=200, msg="success", data=#{page}
     */
    public static <E> Response<Page<E>> page(com.baomidou.mybatisplus.extension.plugins.pagination.Page<E> page) {
        Response<Page<E>> response = new Response<>(SUCCESS.getCode(), SUCCESS.getMsg(), null);
        response.setData(new Page<>(page.getRecords(), page.getTotal()));
        return response;
    }

    /**
     * status=200, code=200, msg="success", data=#{page}
     */
    public static <T, E> Response<Page<E>> page(com.baomidou.mybatisplus.extension.plugins.pagination.Page<T> page, Class<E> clazz) {
        Response<Page<E>> response = new Response<>(SUCCESS.getCode(), SUCCESS.getMsg(), null);
        response.setData(new Page<>(copyList(page.getRecords(), clazz), page.getTotal()));
        return response;
    }

    /**
     * status=200, code=200, msg="success", data=#{page}
     */
    public static <T, E> Response<Page<E>> page(com.baomidou.mybatisplus.extension.plugins.pagination.Page<T> page, Function<T, E> mapper) {
        Response<Page<E>> response = new Response<>(SUCCESS.getCode(), SUCCESS.getMsg(), null);
        response.setData(new Page<>(page.getRecords().stream().map(mapper).collect(Collectors.toList()), page.getTotal()));
        return response;
    }

    /**
     * status=200, code=200, msg="success", data=#{page}
     */
    public static <T, E> Response<Page<E>> page(com.baomidou.mybatisplus.extension.plugins.pagination.Page<T> page, Function<T, E> mapper, Predicate<T> filter) {
        Response<Page<E>> response = new Response<>(SUCCESS.getCode(), SUCCESS.getMsg(), null);
        response.setData(new Page<>(page.getRecords().stream().filter(filter).map(mapper).collect(Collectors.toList()), page.getTotal()));
        return response;
    }

    static <E, T> E copyBean(T src, Class<E> clazz) {
        E target = null;
        try {
            target = clazz.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        BeanUtils.copyProperties(src, target);
        return target;
    }

    static <E, T> List<E> copyList(List<T> srcList, Class<E> clazz) {
        if (CollectionUtils.isEmpty(srcList)) {
            return Collections.emptyList();
        }
        return srcList.stream().map(src -> copyBean(src, clazz)).collect(Collectors.toList());
    }

    @Getter
    @Setter
    public static class Page<E> {

        /**
         * 总数
         */
        private int total;

        /**
         * 列表数据
         */
        private Collection<E> list;

        /**
         * 页码
         */
        @JsonIgnore
        private int page;

        /**
         * 每页行数
         */
        @JsonIgnore
        private int pageSize;

        /**
         * 总页数
         */
        @JsonIgnore
        private int totalPage;

        public Page() {

        }

        public Page(Collection<E> list, Number total) {
            this.list = list;
            this.total = total.intValue();
        }

        public void setTotal(Number total) {
            this.total = total.intValue();
        }

        public void setTotalPage(Number totalPage) {
            this.totalPage = totalPage.intValue();
        }

        public void setPage(Number page) {
            this.page = page.intValue();
        }

        public void setPageSize(Number pageSize) {
            this.pageSize = pageSize.intValue();
        }

        @Override
        public String toString() {
            return "{total=" + total + ", list=" + list + "}";
        }
    }
}
