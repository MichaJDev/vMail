 

vMail is a more advanced Mailing System then you are used to (such as Essentials Mail)

 

vMail lets you reply to emails and forward them to other players on your server.
It's easy to read markup and JSON clickable text implementations make it a great way to mail your friends.

Changelog:
Release 1.0:

Initial Release
Added Sending Replying Forwarding, Inbox Reading and Deleting mail.
Have implemented JSON for easier usage

Release 1.1


Added ReplyAll and cleaned some code.

Release 1.2

Added SpamFilters (Players)
Don't want certain players to mail you add them to your spam list

The mails of the players you've added to your spam list will not come into your inbox but into your spam box.

/Spam command added with additional commands.

Commands:
/mail inbox 
/mail read <id>
/mail del <id>
/mail send <Name> <Subject> <Msg>
/mail reply <id> <Msg>
/mail forward <id> <msg>
/mail replyall <msg>
/spam add <player>
/spam delete <player>
/spam read <id>
/spam del <id>
/spam inbox
Permissions:
vmail.mail
vmail.spam
