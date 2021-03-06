package mca.packets;

import java.util.UUID;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import mca.entity.EntityHuman;
import net.minecraft.entity.player.EntityPlayer;
import radixcore.packets.AbstractPacket;

public class PacketCallVillager extends AbstractPacket implements IMessage, IMessageHandler<PacketCallVillager, IMessage>
{
	private UUID callUUID;
	private boolean callAllRelated;
	
	public PacketCallVillager()
	{
	}

	public PacketCallVillager(boolean callAllRelated)
	{
		this.callUUID = new UUID(0L, 0L);
		this.callAllRelated = true;		
	}

	public PacketCallVillager(UUID callUUID)
	{
		this.callUUID = callUUID;
		this.callAllRelated = false;
	}
	
	@Override
	public void fromBytes(ByteBuf byteBuf)
	{
		this.callUUID = new UUID(byteBuf.readLong(), byteBuf.readLong());
		this.callAllRelated = byteBuf.readBoolean();
	}

	@Override
	public void toBytes(ByteBuf byteBuf)
	{
		byteBuf.writeLong(this.callUUID.getMostSignificantBits());
		byteBuf.writeLong(this.callUUID.getLeastSignificantBits());
		byteBuf.writeBoolean(callAllRelated);
	}

	@Override
	public IMessage onMessage(PacketCallVillager packet, MessageContext context)
	{
		EntityPlayer sender = this.getPlayer(context);
		
		if (packet.callAllRelated)
		{
			for (final Object obj : sender.worldObj.loadedEntityList)
			{
				if (obj instanceof EntityHuman)
				{
					EntityHuman human = (EntityHuman)obj;
					
					if (human.isPlayerAParent(sender) || human.getPlayerSpouse() == sender)
					{
						human.setPositionAndUpdate(sender.posX, sender.posY, sender.posZ);
					}
				}
			}
		}
		
		else
		{
			EntityHuman human = null;
			
			for (Object obj : sender.worldObj.loadedEntityList)
			{
				if (obj instanceof EntityHuman)
				{
					EntityHuman theHuman = (EntityHuman)obj;
					
					if (theHuman.getUniqueID().equals(packet.callUUID))
					{
						human = theHuman;
						break;
					}
				}
			}
			
			if (human != null)
			{
				human.setPositionAndUpdate(sender.posX, sender.posY, sender.posZ);
			}
		}
		
		return null;
	}
}
