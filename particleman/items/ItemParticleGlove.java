package particleman.items;

import particleman.entities.EntityParticleControllable;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemParticleGlove extends Item {

	public ItemParticleGlove(int par1) {
		super(par1);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public boolean onItemUse(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, World par3World, int par4, int par5, int par6, int par7, float par8, float par9, float par10)
    {
		if (!par3World.isRemote) {
			int id = par3World.getBlockId(par4, par5, par6);
			
			if (id != 0) {
				
				
				if (id == Block.torchWood.blockID) {
					
					System.out.println("spawning particle");
					
					EntityParticleControllable particle = new EntityParticleControllable(par3World, par2EntityPlayer.username, 0);
					particle.setPosition(par4+0.5F, par5+1.6F, par6+0.5F);
					
					par3World.spawnEntityInWorld(particle);
				}
			}
		}
		
        return false;
    }
	
	@Override
	public void onUpdate(ItemStack par1ItemStack, World par2World, Entity par3Entity, int par4, boolean par5) {
		
	}
	
	@Override
	public ItemStack onItemRightClick(ItemStack par1ItemStack, World par2World, EntityPlayer par3EntityPlayer) {
		return par1ItemStack;
	}
	
	@Override
	public int getMaxItemUseDuration(ItemStack par1ItemStack)
    {
        return 72000;
    }
	
	@Override
	public EnumAction getItemUseAction(ItemStack par1ItemStack)
    {
        return EnumAction.bow;
    }

}
