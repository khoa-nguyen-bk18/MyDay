# Daily Note path resolution order

MyDay resolves a date’s Daily Note inside an Obsidian Vault by reading Periodic Notes daily settings first, then core `.obsidian/daily-notes.json`, then falling back to vault-root `YYYY-MM-DD.md`. We chose this over core-only or MyDay-only path settings because Periodic Notes is a common replacement for core Daily Notes, and wrong-file resolution would silently reflect the wrong note.
