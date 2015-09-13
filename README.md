#jmork - A Java Implementation of a Mork Parser

- Written by Mike Haller 
- Contributions by Wolfgang Fahl - [BITPlan GmbH](http://www.bitplan.com)
- Contributions by Ingo Bauersachs for [Jitsi](https://jitsi.org)

## About
- Mork is a text "database" format used by Mozilla
  applications like Firefox and Thunderbird to store
  some data. Mainly the URL History of Firefox and
  the address book of Thunderbird is stored in the
  (rather weird) Mork format.
- This is a Java Implementation which can be used to
  parse .mab Files on a raw level.
- The current version of the data format is v1.4.

##Primary goals of this project
- Parse the Mork format to be able to read in a
  Thunderbird Addressbook
- Provide an initial open-source place for
  Mork-related tools implemented in Java

## Main Drawbacks of Mork
- Rather complex, not human readable, not space
  saving, weird referencing, multiple escape
  characters, C-style and HTML-style comments are
  mixed.
- Is going to be replaced by a more general standard 

## Ideas which could be implemented in the future
- Implement an address book parser on top of the mork
  implementation to parse Mozilla Thunderbird address
  books. Or even write the files.

## More information about Mork
- [Mork What Is It](https://developer.mozilla.org/en-US/docs/Mozilla/Tech/Mork/What_is_it)
- Mork file format description on [Wikipedia](https://en.wikipedia.org/wiki/Mork_%28file_format%29)
- [When the database worms eat into your brain](https://www.jwz.org/blog/2004/03/when-the-database-worms-eat-into-your-brain/)
- [Official Specification of the Mork 1.4 format](https://developer.mozilla.org/en-US/docs/Mozilla/Tech/Mork/Structure)
- [Proposal of the Mork format](http://www-archive.mozilla.org/mailnews/arch/mork/primer.txt)
- [A Mork parser in Perl](https://www.jwz.org/hacks/mork.pl)
- [Source repository of Mozilla Database stuff](http://lxr.mozilla.org/seamonkey/source/db/)
