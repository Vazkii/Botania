/**
 * This class was created by <SoundLogic>. It's distributed as
 * part of the Botania Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 * 
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 * 
 * File Created @ [Apr 18, 2015, 7:30:00 PM (GMT)]
 */
package vazkii.botania.api.mana;

import net.minecraft.world.World;

/**
 * Any TileEntity that implements this is considered a Mana Spreader,
 * by which can fire mana bursts as a spreader.<br>
 * 
 */
public interface IManaSpreader extends IManaBlock {

	public float getRotationX();
	
	public float getRotationY();
	
	public void setCanShoot(boolean canShoot);
	
	public int getBurstParticleTick();
	
	public void setBurstParticleTick(int i);
	
	public int getLastBurstDeathTick();
	
	public void setLastBurstDeathTick(int ticksExisted);

}
