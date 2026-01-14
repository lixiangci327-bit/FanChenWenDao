package net.Lcing.fanchenwendao.client.fx;

import com.lowdragmc.photon.client.fx.FX;
import com.lowdragmc.photon.client.fx.FXEffectExecutor;
import com.lowdragmc.photon.client.gameobject.IFXObject;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import yesman.epicfight.api.animation.Joint;
import yesman.epicfight.api.animation.Pose;
import yesman.epicfight.api.client.physics.cloth.ClothSimulatable;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.api.utils.math.Vec3f;
import yesman.epicfight.client.world.capabilites.entitypatch.player.LocalPlayerPatch;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

public class BoneExecutor extends FXEffectExecutor {

    private final LivingEntity entity;
    private final String jointName;
    private final boolean followRotation;



    //构造函数
    public BoneExecutor(FX fx, Level level, LivingEntity entity, String jointName, boolean followRotation) {
        super(fx, level);
        this.entity = entity;
        this.jointName = jointName;
        this.followRotation = followRotation;
    }

    @Override
    public void start() {
        if (!entity.isAlive()) return;

        //创建运行时
        this.runtime = fx.createRuntime();

        //设置初始位置
        updateTransform(1.0f);//1.0f代表当前时刻

        this.runtime.emmit(this, delay);
    }


    @Override
    public void updateFXObjectFrame(IFXObject fxObject, float partialTicks) {

        //控制根节点
        if (runtime != null && fxObject == runtime.root) {
            if (!entity.isAlive()) {
                runtime.destroy(true);//实体死亡清除特效
                return;
            }

            updateTransform(partialTicks);
        }
    }

    private void updateTransform(float partialTicks) {

        //读取epic补丁
        LivingEntityPatch<?> patch = EpicFightCapabilities.getEntityPatch(entity, LocalPlayerPatch.class);
        if (patch == null) return;

        //获取动画姿态Pose
        Pose pose = patch.getAnimator().getPose(partialTicks);
        Armature armature = patch.getArmature();

        //寻找骨骼
        Joint joint = armature.searchJointByName(jointName);
        if (joint == null) return;

        //关节变换矩阵
        OpenMatrix4f modelTransform = armature.getBoundTransformFor(pose, joint);

        //转换到游戏世界内
        //Photon的Vector3f是JOML的，EF有单独的Vecf3f，需要手动转换
        Vec3f efPos = modelTransform.toTranslationVector();

        //计算旋转：先应用关节本身的旋转，再叠加上玩家身体的旋转
        //获取玩家身体的旋转
        float yRot;
        if (patch instanceof ClothSimulatable simulatable) {
            yRot = simulatable.getAccurateYRot(partialTicks);
        } else {
            yRot = Mth.lerp(partialTicks, entity.yBodyRotO, entity.yBodyRot);
        }

        yRot += 180.0f;

        //将局部坐标 根据玩家朝向进行世界旋转
        OpenMatrix4f entityRotationMatrix = OpenMatrix4f.createRotatorDeg(-yRot, Vec3f.Y_AXIS);
        OpenMatrix4f.transform3v(entityRotationMatrix, efPos, efPos);

        //加上身体的实际坐标
        double entityX = Mth.lerp(partialTicks, entity.xo, entity.getX());
        double entityY = Mth.lerp(partialTicks, entity.yo, entity.getY());
        double entityZ = Mth.lerp(partialTicks, entity.zo, entity.getZ());

        //设置photon位置
        runtime.root.updatePos(new Vector3f(
                (float) (entityX + efPos.x + offset.x),
                (float) (entityY + efPos.y + offset.y),
                (float) (entityZ + efPos.z + offset.z)
        ));

        //处理旋转跟随
        if (followRotation) {

            //获取骨骼旋转Quaternion
            //转换EF矩阵->JOML四元数
            OpenMatrix4f worldMatrix = modelTransform.mulFront(entityRotationMatrix);

            //wordlRot是骨骼当前在世界里的朝向
            Quaternionf worldRot = worldMatrix.toQuaternion();

            runtime.root.updateRotation(worldRot.mul(this.rotation));
        }


    }
}

