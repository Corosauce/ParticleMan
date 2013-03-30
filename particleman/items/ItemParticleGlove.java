package particleman.items;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import particleman.entities.EntityParticleControllable;
import particleman.forge.ParticleMan;

public class ItemParticleGlove extends Item {

	public HashMap<String, List<EntityParticleControllable>> playerParticles = new HashMap<String, List<EntityParticleControllable>>();
	
	public ItemParticleGlove(int par1) {
		super(par1);
		// TODO Auto-generated constructor stub
	}
	
	public void check(String parUser) {
		if (!playerParticles.containsKey(parUser)) playerParticles.put(parUser, new LinkedList<EntityParticleControllable>());
	}
	
	@Override
	public boolean onItemUse(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, World par3World, int par4, int par5, int par6, int par7, float par8, float par9, float par10)
    {
		
		System.out.println("woooop");
		
		if (!par3World.isRemote) {
			check(par2EntityPlayer.username);
			int id = par3World.getBlockId(par4, par5, par6);
			
			if (id != 0) {
				int spawnType = -1;
				if (id == Block.torchWood.blockID) {
					spawnType = 0;
				} else if (id == Block.torchRedstoneActive.blockID) {
					spawnType = 1;
				}
				
				if (spawnType != -1) {
					EntityParticleControllable particle = new EntityParticleControllable(par3World, par2EntityPlayer.username, spawnType);
					particle.setPosition(par4+0.5F, par5+1.6F, par6+0.5F);
					particle.index = playerParticles.get(par2EntityPlayer.username).size();
					playerParticles.get(par2EntityPlayer.username).add(particle);
					par3World.spawnEntityInWorld(particle);
				}
			}
		}
		
        return true;
    }
	
	@Override
	public void onUpdate(ItemStack par1ItemStack, World par2World, Entity par3Entity, int par4, boolean par5) {
		//System.out.println(par5);
		if (!par2World.isRemote && par3Entity instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer)par3Entity;
			check(player.username);

			for (int i = 0; i < playerParticles.get(player.username).size(); i++) {
				EntityParticleControllable particle = playerParticles.get(player.username).get(i);
				
				if (particle == null || particle.isDead || par3Entity.getDistanceToEntity(particle) > 50) {
					playerParticles.get(player.username).remove(particle);
				} else {
					//ParticleMan.spinAround(particle, player, 10F, 0.5F, 2F, particle.index, 0.02F, 1);
					particle.decayTime = 0;
				}
			}
			
		} else { return; }
	}
	
	@Override
	public ItemStack onItemRightClick(ItemStack par1ItemStack, World par2World, EntityPlayer par3EntityPlayer) {
		if (!par2World.isRemote) check(par3EntityPlayer.username);
		
		System.out.println("eeeeeee");
		
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
