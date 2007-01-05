A Java Implementation of a Mork Parser
written by Mike Haller <mike.haller@smartwerkz.com>

About
* Mork is a text "database" format used by Mozilla applications like
  Firefox and Thunderbird to store some data. Mainly the URL History of Firefox
  and the address book of Thunderbird is stored in the (rather weird) Mork 
  format.
* This is a Java Implementation which can be used to parse .mab Files
  on a raw level.
* The current version of the data format is v1.4.

Primary Goals of this project
* Parse the Mork format to be able to read in a Thunderbird Addressbook
* Provide an initial open-source place for Mork-related tools implemented in Java

Main Drawbacks of Mork
* Rather complex, not human readable, not space saving, weird referencing,
  multiple escape characters, C-style and HTML-style comments are mixed.
* Is going to be replaced by a more general standard 
 
Future
* Implement an Address Book parser on top of the mork implementation to
  parse Mozhilla Thunderbird address books. Or even write the files.

More Information about Mork:
* Official Specification of the Mork 1.4 format:
  http://developer.mozilla.org/en/docs/Mork_Structure
* Proposal of the Mork format
  http://www.mozilla.org/mailnews/arch/mork/primer.txt
* Wikipedia about Mork
  http://en.wikipedia.org/wiki/Mork_(file_format)
* A Mork parser in Perl
  http://www.jwz.org/hacks/mork.pl
