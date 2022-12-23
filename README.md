## Album Tracker
When recording and producing musical releases, there can be a lot of steps in the process, and a traditional method for keeping track is a grid drawn on a whiteboard - list of songs down one side, list of tasks (drums, bass, guitars etc) along the top. This has the advantage of being simple to implement, and making it satisfying to check tasks off.

However, as the process goes on and tasks need to be added for specific songs, or subdivided into sub-tasks, you have to either squeeze additional columns into the grid or continually redraw it, and your neat progress tracker becomes illegible and annoying. It's also not much use if you want to check your progress from elsewhere.

I built Album Tracker with a view to providing a flexible, legible, and expandable progress grid which can handle subtasks, including a multi-user comment history and a scratch-pad for notes. The structure is as follows: each Album contains Tasks - sorted into ordered (and reorderable) Phases - and Songs, to which you can assign any number of these tasks. Tasks can then be subdivided into Subtasks on a per-song basis (let's say most of the songs on your album have a single vocal track, but one is a duet - you can just subdivide the Vocals task without detracting from the immediate legibility of your progress grid. Once you do a Task, mark it as complete; subdivided Tasks will only be marked as complete once all their Subtasks are also complete.

You can drill down into each individual Song to see only the tasks assigned to it, along with its Comments and Notes sections, and share access with your collaborators in the Edit panel. Users will receive an email invite link to work with you on the Album, and collaborators can be removed if needed. Don't worry - only the user who created the Album is able to delete Songs, Phases or Tasks.

Try it out at https://album-tracker-358914.nw.r.appspot.com/

# Stack
Java, Spring Boot, Thymeleaf, Hibernate, Google Datastore, OAuth2, Spring Security, MailJet