package com.example.activity.memo;

import java.io.Serializable;

/**
 * List的一条备忘录对象
 * 实现序列化接口，用于Activity传递（修改备忘录）
 *      参考：https://blog.csdn.net/leejizhou/article/details/51105060
 */
public class Message implements Serializable {
    private Long id;
    private String time;
    private String content;
    public Message(Long id, String time, String content){
        this.id = id;
        this.time = time;
        this.content = content;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public Long getId() {
        return id;
    }
    public void setTime(String time) {
        this.time = time;
    }
    public String getTime() {
        return time;
    }
    public void setContent(String content) {
        this.content = content;
    }
    public String getContent() {
        return content;
    }
}
