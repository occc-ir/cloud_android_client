/*
Yaaic - Yet Another Android IRC Client

Copyright 2009-2013 Sebastian Kaspari

This file is part of Yaaic.

Yaaic is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

Yaaic is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with Yaaic.  If not, see <http://www.gnu.org/licenses/>.
 */
package ir.occc.android.irc.command.handler;

import ir.occc.android.irc.command.BaseHandler;
import ir.occc.android.irc.exception.CommandException;

import java.util.Collection;

import ir.occc.android.R;
import ir.occc.android.irc.IRCService;
import ir.occc.android.model.Broadcast;
import ir.occc.android.model.Conversation;
import ir.occc.android.model.Message;
import ir.occc.android.model.Server;

import android.content.Context;
import android.content.Intent;

/**
 * Command: /amsg <message>
 * 
 * Send a message to all channels on the server
 * 
 * @author Sebastian Kaspari <sebastian@yaaic.org>
 */
public class AMsgHandler extends BaseHandler
{
    /**
     * Execute /amsg
     */
    @Override
    public void execute(String[] params, Server server, Conversation conversation, IRCService service) throws CommandException
    {
        if (params.length > 1) {
            String text = BaseHandler.mergeParams(params);

            Collection<Conversation> mConversations = server.getConversations();

            for (Conversation currentConversation : mConversations) {
                if (currentConversation.getType() == Conversation.TYPE_CHANNEL) {
                    Message message = new Message("<" + service.getConnection(server.getId()).getNick() + "> " + text);
                    currentConversation.addMessage(message);

                    Intent intent = Broadcast.createConversationIntent(
                        Broadcast.CONVERSATION_MESSAGE,
                        server.getId(),
                        currentConversation.getName()
                    );

                    service.sendBroadcast(intent);

                    service.getConnection(server.getId()).sendMessage(currentConversation.getName(), text);
                }
            }
        } else {
            throw new CommandException(service.getString(R.string.invalid_number_of_params));
        }
    }

    /**
     * Usage of /amsg
     */
    @Override
    public String getUsage()
    {
        return "/amsg <message>";
    }

    /**
     * Description of /amsg
     */
    @Override
    public String getDescription(Context context)
    {
        return context.getString(R.string.command_desc_amsg);
    }
}
