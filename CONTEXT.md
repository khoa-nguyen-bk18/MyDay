# MyDay — Daily Reflection

Local-first journaling companion that turns an Obsidian daily note into a private, AI-assisted reflection draft the user can review and save back into their vault. In the app shell, Reflect is a primary tab for setup and reviewing today’s Draft.


## Language

**Vault**:
The user-picked Obsidian folder MyDay is granted access to via the system folder picker.
_Avoid_: workspace, library, storage root

**Daily Note**:
The markdown file for a calendar date. Path resolution order: Periodic Notes daily settings, then core Obsidian Daily Notes (`.obsidian/daily-notes.json`), then vault-root `YYYY-MM-DD.md`.
_Avoid_: journal entry, diary page, today’s file

**Reflection**:
The structured markdown artifact produced for a date (Balanced sections), distinct from the Daily Note’s own writing.
_Avoid_: summary, AI note, review

**Draft**:
An unsaved Reflection held only inside MyDay for a date until the user explicitly Saves it to the Vault.
_Avoid_: temp file, cache note, preview

**Embed Link**:
An Obsidian wiki embed (`![[...]]`) appended to the Daily Note that points at the saved Reflection file in the Reflection Folder. On re-Save to the same path, MyDay replaces the Reflection file after confirmation and does not rewrite the Daily Note.
_Avoid_: hyperlink, attachment, inline paste

**Reflection Folder**:
A configurable folder in the Vault (default `reflections/`) where saved Reflection files are stored as `YYYY-MM-DD.md`, independent of the Daily Note’s nested path.
_Avoid_: attachments, AI folder, daily folder

**Source Snapshot**:
The Daily Note text (and content hash) used when a Draft was generated; used to hint when the note has changed since.
_Avoid_: payload, prompt context (implementation)

**Sufficient Content**:
Daily Note text that, after trimming whitespace, still has enough non-empty substance (e.g. above a character threshold and not only headings/empty checkboxes) to justify generating a Reflection. MyDay does not create a missing Daily Note. Oversized notes are truncated from the end (most recent writing) up to a configured cap, with disclosure that only part was included.
_Avoid_: valid journal, meaningful day (vague)

**Auto-Draft**:
A background attempt to create a Draft for the current date inside the user’s local time window when all gates pass; a successful Draft is not overwritten automatically, but failed attempts may retry later in the same window.
_Avoid_: sync, background summary, silent save

**OpenRouter Key**:
The user’s own OpenRouter API credential stored only on-device; MyDay has no app-hosted model. A default model slug is used unless the user overrides it.
_Avoid_: app API key, cloud subscription

**Reflection Feedback**:
Optional Helpful / Not helpful signal on a Draft or saved Reflection, with optional reason chips; recorded as analytics without journal or reflection text.
_Avoid_: rating, review, bug report
