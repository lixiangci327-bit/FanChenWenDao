package net.Lcing.fanchenwendao.fashu.logic;


import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@EventBusSubscriber(modid = "fanchenwendao")
public class FaShuDelayScheduler {

    //任务清单
    private static final List<DelayedTask> tasks = new ArrayList<>();

    //定义任务结构
    private static class DelayedTask {
        int remainingTicks; //剩余时间
        Runnable action;    //函数式接口，存放延迟执行的逻辑（伤害，效果）

        //构造函数，创建一个新任务时调用
        public DelayedTask(int remainingTicks, Runnable action) {
            this.remainingTicks = remainingTicks;
            this.action = action;
        }

    }

    //对外接口，安排一个新任务
    public static void schedule(int ticks, Runnable action) {
        tasks.add(new DelayedTask(ticks, action));
    }

    //监听服务器tick
    @SubscribeEvent
    public static void onServerTick(ServerTickEvent.Post event) {
        //性能优化，若无人施法，直接跳过
        if (tasks.isEmpty()) return;

        //使用迭代器遍历列表：遍历过程中可能需要删除任务，使用for循环会抛出异常
        Iterator<DelayedTask> iterator = tasks.iterator();

        while (iterator.hasNext()) {
            //获取下一个任务
            DelayedTask task = iterator.next();
            //倒计时减一
            task.remainingTicks--;
            //检查时间是否到了
            if (task.remainingTicks == 0) {
                try {
                    //运行Runnable的逻辑
                    task.action.run();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                //任务执行完毕，从列表中移除
                iterator.remove();
            }
        }
    }
}
