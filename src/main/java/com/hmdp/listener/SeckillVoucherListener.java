package com.hmdp.listener;

import cn.hutool.json.JSONUtil;
import com.hmdp.entity.VoucherOrder;
import com.hmdp.service.impl.SeckillVoucherServiceImpl;
import com.hmdp.service.impl.VoucherOrderServiceImpl;
import com.rabbitmq.client.Channel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Slf4j
//@Component
//@RequiredArgsConstructor
public class SeckillVoucherListener {
    @Resource
    SeckillVoucherServiceImpl seckillVoucherService;
    @Resource
    VoucherOrderServiceImpl voucherOrderService;

    // TODO 这里都没有做seckill.lua脚本的业务，需要再去查一次数据库看看订单存不存在，感觉存在问题。
    //  不能保证原子性，可以添加 @Transactional事务注解

    /**
     * sheng  消费者1
     *
     */
    @RabbitListener(queues = "QA")
    public void receivedA(Message message, Channel channel) throws Exception {
        String msg = new String(message.getBody());
        log.info("正常队列:");
        VoucherOrder voucherOrder = JSONUtil.toBean(msg, VoucherOrder.class);
        log.info("订单信息：" + voucherOrder.toString());
        voucherOrderService.save(voucherOrder);//保存到数据库
        //数据库秒杀库存减一
        Long voucherId = voucherOrder.getVoucherId();
        seckillVoucherService.update()
                .setSql("stock = stock - 1") // set stock = stock - 1
                .eq("voucher_id", voucherId).gt("stock", 0) // where id = ? and stock > 0
                .update();
    }

    /**
     * sheng  消费者2
     *
     * @param message
     * @throws Exception
     */
    @RabbitListener(queues = "QD")
    public void receivedD(Message message) throws Exception {
        log.info("死信队列:");
        String msg = new String(message.getBody());
        VoucherOrder voucherOrder = JSONUtil.toBean(msg, VoucherOrder.class);
        log.info("订单信息：" + voucherOrder.toString());
        voucherOrderService.save(voucherOrder);
        Long voucherId = voucherOrder.getVoucherId();
        seckillVoucherService.update()
                .setSql("stock = stock - 1") // set stock = stock - 1
                .eq("voucher_id", voucherId).gt("stock", 0) // where id = ? and stock > 0
                .update();
    }
}