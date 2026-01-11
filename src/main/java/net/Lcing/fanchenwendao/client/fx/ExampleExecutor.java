package net.Lcing.fanchenwendao.client.fx;

import com.lowdragmc.photon.client.fx.FX;
import com.lowdragmc.photon.client.fx.FXRuntime;
import com.lowdragmc.photon.client.fx.IEffectExecutor;
import com.lowdragmc.photon.client.gameobject.IFXObject;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.client.event.ViewportEvent;
import org.joml.Vector3f;

import javax.annotation.Nullable;

//IEffectExecutor 是接口，必须用 implements
public class ExampleExecutor implements IEffectExecutor {
    public final FX fx;
    public final Level level;

    // 运行时实例
    @Nullable
    private FXRuntime fxRuntime;

    //控制运动变量
    //出发点
    private Vector3f startPos = new Vector3f();
    //目标点
    private Vector3f endPos = new Vector3f();
    //开始时间
    private long startTime;
    //动画时间
    private long durationMs;
    //动画控制开关
    private boolean isMoving = false;

    //延迟发射
    private boolean isWaiting = false;
    private long waitDurationMs;

    public ExampleExecutor(FX fx, Level level) {
        this.fx = fx;
        this.level = level;
    }

    // 实现接口方法：提供特效所在的世界
    @Override
    public Level getLevel() {
        return this.level;
    }


    //发射特效
    public void emit() {
        kill();
        fxRuntime = fx.createRuntime();
        fxRuntime.emmit(this);
    }


    //销毁特效
    public void kill() {
        if (fxRuntime == null) return;
        fxRuntime.destroy(true);
        fxRuntime = null;
    }

    //移动动画方法
    public void moveTo(double targetX, double targetY, double targetZ, long durationMs, long delayMs) {
        if (fxRuntime == null) return;
        //获取root对象
        IFXObject root = fxRuntime.getRoot();
        //把root当前坐标作为起点
        this.startPos.set(root.transform().position());
        //记录终点
        this.endPos.set((float) targetX, (float) targetY, (float) targetZ);
        //设置时间和开关
        this.startTime = System.currentTimeMillis();
        this.waitDurationMs = delayMs;
        this.durationMs = durationMs;

        this.isWaiting = true;
        this.isMoving = false;
    }

    //设置位置
    public void setPosition(double x, double y, double z) {
        if (fxRuntime == null) return;

        IFXObject root = fxRuntime.getRoot();

        root.updatePos(new Vector3f((float) x, (float) y, (float) z));
    }


    @Override
    public void updateFXObjectFrame(IFXObject fxObject, float partialTicks) {
        //如果为处于移动状态，或者为空，直接返回
        if (fxRuntime == null) return;

        //特效等待
        if (isWaiting) {
            long now = System.currentTimeMillis();
            if(now - startTime >= waitDurationMs){
                isWaiting = false;
                isMoving = true;
                startTime = now;
            }else{
                if(fxObject == fxRuntime.getRoot()){
                    fxObject.updatePos(startPos);
                }
                return;
            }
        }

        if (!isMoving) return;

        //单独控制root
        if (fxObject != fxRuntime.getRoot()) return;

        //计算警告的时间
        long currentTime = System.currentTimeMillis();
        long elapsed = currentTime - startTime;

        //计算进度
        float t = (float) elapsed / (float) durationMs;

        if (t >= 1.0f) {
            fxObject.updatePos(endPos);
            isMoving = false;

            kill();
        }else{
            Vector3f currentPos = new Vector3f();
            //使用线性插值
            startPos.lerp(endPos, t, currentPos);

            //更新位置
            fxObject.updatePos(currentPos);
        }
    }
}