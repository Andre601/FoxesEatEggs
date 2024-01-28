package ch.andre601.foxes_eat_eggs.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.entity.*;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.FoxEntity;
import net.minecraft.item.EggItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FoxEntity.class)
abstract class MixinFoxEntity extends AnimalEntity implements VariantProvider<FoxEntity.Variant>{

	@Shadow
	public abstract void readCustomDataFromNbt(NbtCompound nbt);

	@Shadow
	public abstract void setTarget(@Nullable LivingEntity target);

	@Shadow
	private int eatingTime;

	protected MixinFoxEntity(EntityType<? extends AnimalEntity> entityType, World world){
		super(entityType, world);
	}

	@Inject(
		method = "canPickupItem",
		at = @At("RETURN"),
		cancellable = true
	)
	private void modifyCanPickupItem(ItemStack itemStack, CallbackInfoReturnable<Boolean> cir){
		if(cir.getReturnValueZ())
			return;

		Item item = itemStack.getItem();
		ItemStack stack = this.getEquippedStack(EquipmentSlot.MAINHAND);

		cir.setReturnValue(
			stack.isEmpty() || this.eatingTime > 0 && (item.isFood() || (item instanceof EggItem)) && (!stack.getItem().isFood() && !(stack.getItem() instanceof EggItem))
		);
	}

	@ModifyExpressionValue(
		method = "tickMovement",
		at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/passive/FoxEntity;canEat(Lnet/minecraft/item/ItemStack;)Z")
	)
	private boolean canEat(boolean original){
		ItemStack stack = this.getEquippedStack(EquipmentSlot.MAINHAND);
		if(stack == null)
			return original;

		return (stack.getItem() instanceof EggItem) || original;
	}

	@ModifyExpressionValue(
		method = "tickMovement",
		at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;finishUsing(Lnet/minecraft/world/World;Lnet/minecraft/entity/LivingEntity;)Lnet/minecraft/item/ItemStack;")
	)
	private ItemStack returnItemStack(ItemStack original){
		if(!(original.getItem() instanceof EggItem))
			return original.finishUsing(this.getWorld(), this);

		original.decrement(1);

		return original;
	}
}
