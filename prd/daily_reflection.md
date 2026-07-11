# Product Requirements Document

## Daily Reflection

**Status:** Draft
**Owner:** Product Team
**Target platform:** Mobile application
**Feature type:** AI-assisted journaling
**Working name:** Daily Reflection
**Alternative names:** Daily Contemplation, Day in Review, Reflect on Today

---

## 1. Product Overview

Daily Reflection helps users pause, understand, and learn from their day by transforming their journal content into a thoughtful reflection.

Instead of only summarizing what happened, the feature identifies meaningful moments, emotions, patterns, progress, unresolved concerns, and possible lessons from the user’s journal entries.

The intended value is not:

> “Here is what you wrote today.”

The intended value is:

> “Here is what your day may mean, what stood out, and what may be worth carrying forward.”

The reflection should feel supportive, grounded, private, and non-judgmental. It should help users develop self-awareness without pretending to diagnose them or claiming certainty about their emotional state.

---



## 2. Problem Statement

Users often write daily notes, capture thoughts, or record events, but do not consistently revisit or reflect on what they wrote.

This creates several problems:

- Journal entries become an archive rather than a tool for personal growth.
- Important emotions, concerns, or achievements can be buried among routine notes.
- Users may struggle to identify recurring patterns across their experiences.
- Writing a structured daily reflection manually requires time and mental effort.
- Simple summaries do not provide enough emotional or reflective value.

Users need an easy way to convert fragmented journal content into a meaningful reflection that helps them better understand their day.

---



## 3. Product Vision

Enable users to end each day with a clearer understanding of:

- What happened
- What mattered
- How they may have felt
- What they handled well
- What remains unresolved
- What they may want to remember or do differently tomorrow

Daily Reflection should become a small, trustworthy ritual that helps users turn daily journaling into long-term self-awareness.

---



## 4. Goals



### 4.1 Primary Goals

1. Help users understand the meaning of their day, not only the sequence of events.
2. Reduce the effort required to create a structured daily reflection.
3. Encourage users to revisit and engage with their journal entries.
4. Help users recognize achievements, challenges, emotions, and lessons.
5. Create a recurring reason for users to return to the application.



### 4.2 Secondary Goals

1. Increase the number of days users actively journal.
2. Improve retention through a meaningful daily ritual.
3. Enable future weekly, monthly, and long-term reflection features.
4. Provide users with actionable prompts for the next day.
5. Strengthen the perceived value of the user’s accumulated journal data.

---



## 5. Non-Goals

The initial version will not:

- Provide medical or psychological diagnoses.
- Replace therapy, counseling, or professional mental-health support.
- Determine the user’s emotional state with certainty.
- Automatically modify the user’s original journal entries.
- Generate long-term personality profiles.
- Compare users with other users.
- Publish or share reflections without explicit user action.
- Generate reflections from private sources the user has not selected.
- Automatically execute tasks mentioned in the reflection.

---



## 6. Target Users



### 6.1 Primary Users

**Consistent journalers**

Users who write daily notes and want additional value from their entries.

**Casual journalers**

Users who capture fragmented thoughts, tasks, events, images, or voice notes but do not write formal reflections.

**Productivity-focused users**

Users who want to review progress, challenges, decisions, and unfinished work.

**Self-improvement users**

Users interested in mindfulness, emotional awareness, habit building, and personal growth.

### 6.2 Example User Needs

- “Help me understand what mattered today.”
- “Remind me that I made progress, even if the day felt unproductive.”
- “Show me what kept occupying my attention.”
- “Give me something thoughtful to consider before tomorrow.”
- “Turn my fragmented notes into a coherent reflection.”
- “Help me notice recurring stress or positive moments.”

---



## 7. Jobs to Be Done



### Functional Job

When I have written notes throughout the day, I want the application to review them and create a structured reflection so that I do not need to reread and interpret everything manually.

### Emotional Job

When my day feels confusing, busy, or unproductive, I want a grounded perspective that helps me recognize what mattered and feel more aware of my experience.

### Long-Term Job

When I review my journal over time, I want to see meaningful reflections that help me understand my progress, habits, decisions, and recurring patterns.

---



## 8. Core User Value

The feature should provide five layers of value:

### 8.1 Recall

Help the user remember the important events and thoughts from the day.

### 8.2 Interpretation

Identify why certain moments may have been meaningful.

### 8.3 Emotional Awareness

Reflect emotional signals expressed in the journal without making unsupported assumptions.

### 8.4 Learning

Surface possible lessons, achievements, tensions, or unresolved matters.

### 8.5 Continuity

Help the user connect today with tomorrow through an intention, question, or next step.

---



## 9. User Stories



### Core User Stories

- As a user, I want to generate a reflection from today’s journal so I can understand my day without rereading everything.
- As a user, I want the reflection to be based only on content I have written.
- As a user, I want to see the important moments, emotions, achievements, and challenges from my day.
- As a user, I want to regenerate the reflection when the result is not useful.
- As a user, I want to edit the generated reflection before saving it.
- As a user, I want to save the reflection into my journal.
- As a user, I want to control which content is included.
- As a user, I want to know when there is not enough content to generate a meaningful reflection.
- As a user, I want my private journal content to remain protected.
- As a user, I want to choose the tone and depth of the reflection.



### Future User Stories

- As a user, I want to compare daily reflections across a week.
- As a user, I want to identify recurring themes across multiple days.
- As a user, I want to receive a reminder to reflect at the end of the day.
- As a user, I want the reflection style to adapt to my preferred journaling approach.

---



## 10. Feature Scope



## 10.1 MVP Scope

The MVP will allow the user to:

1. Open the Daily Reflection feature from a daily note or home screen.
2. Review the journal content that will be used.
3. Generate a reflection for a selected date.
4. View a structured reflection.
5. Edit the generated result.
6. Save it into the daily note or a dedicated reflection location.
7. Regenerate the result.
8. Provide lightweight feedback on reflection quality.
9. Control whether the reflection is generated automatically or manually.
10. Delete generated reflection data.



## 10.2 Post-MVP Scope

Potential later capabilities:

- Weekly and monthly reflections
- Recurring-theme detection
- Mood trends
- Reflection history
- Personalized reflection templates
- Voice-based reflection
- Reflection using images or attachments
- Follow-up conversational questions
- End-of-day reminders
- On-device generation
- Multiple AI providers through bring-your-own-key
- Reflection streaks
- Search across past reflections
- Connections between goals and daily activity

---



## 11. Entry Points

Users may access Daily Reflection through:

### Primary Entry Point

A **Reflect on Today** action inside the current daily note.

### Secondary Entry Points

- Home screen reflection card
- Daily note overflow menu
- Calendar or journal history
- End-of-day notification
- Empty-state suggestion after sufficient journal content exists
- Long press or contextual action on a daily note

The MVP should prioritize the daily-note entry point because it gives the feature clear context and reduces navigation complexity.

---



## 12. Primary User Flow



### 12.1 Generate Reflection

1. User opens today’s daily note.
2. User selects **Reflect on Today**.
3. Application collects eligible content for the selected date.
4. Application displays a preview of the source content.
5. User may include or exclude sections.
6. User selects **Generate Reflection**.
7. Application processes the selected content.
8. Application displays the generated reflection.
9. User reviews, edits, regenerates, or saves it.
10. Reflection is inserted into the selected destination.



### 12.2 Save Reflection

The user can choose one of the following destinations:

- Append to the current daily note
- Insert under a configured heading
- Create a separate reflection note
- Copy to clipboard
- Keep temporarily without saving

For the MVP, the default behavior should be:

> Insert the reflection under a configurable heading in the selected daily note.

Example:

```markdown
## Daily Reflection

Today seemed to revolve around...
```

---



## 13. Reflection Output Structure

The default reflection should be structured but natural.

### Recommended Default Format



#### Today at a Glance

A concise interpretation of the day in two to four sentences.

#### What Stood Out

The most meaningful events, thoughts, decisions, or interactions.

#### Emotional Undercurrent

A careful description of emotions explicitly expressed or reasonably suggested by the journal.

#### Wins and Progress

Achievements, completed tasks, positive decisions, or signs of resilience.

#### Challenges or Tensions

Difficulties, frustrations, unfinished matters, or competing priorities.

#### What This Day May Be Teaching You

One or two grounded observations based on the source content.

#### Something to Carry Into Tomorrow

A practical intention, reminder, or reflective question.

### Example Output

```markdown
## Daily Reflection

### Today at a Glance

Today appears to have been shaped by a mix of steady progress and mental pressure. You completed several practical tasks, but your notes suggest that one unresolved decision continued to occupy your attention.

### What Stood Out

The strongest theme was your effort to move the project forward despite unclear requirements. The conversation with the team seemed important because it gave you more direction, even though some uncertainty remains.

### Emotional Undercurrent

You expressed frustration around the delay, but there was also a sense of persistence. Rather than avoiding the issue, you continued gathering information and looking for a workable path.

### Wins and Progress

You clarified the next implementation step and completed the task you had been postponing. That may be more meaningful progress than the day initially felt like.

### Challenges or Tensions

The unresolved dependency is still creating pressure. Your notes also suggest that you may be carrying several concerns at once without clearly separating what is actionable from what is outside your control.

### What This Day May Be Teaching You

Today may be a reminder that progress does not always mean completing everything. Sometimes it means reducing uncertainty enough to take the next step.

### Something to Carry Into Tomorrow

Identify the single unresolved question that would unlock the most progress, and address that before taking on additional work.
```

---



## 14. Reflection Modes

Users should eventually be able to select a reflection style.

### 14.1 Balanced Reflection

Covers events, emotions, achievements, challenges, and lessons.

This is the default MVP mode.

### 14.2 Mindful Reflection

Focuses on emotions, presence, gratitude, and internal experience.

### 14.3 Productivity Reflection

Focuses on progress, obstacles, decisions, unfinished work, and tomorrow’s priorities.

### 14.4 Concise Reflection

Produces a short reflection of approximately 100–150 words.

### 14.5 Deep Reflection

Produces a more detailed interpretation with additional reflective questions.

Only Balanced Reflection is required for MVP. Concise and Deep may be added if implementation cost is low.

---



## 15. Content Selection

The system should clearly show which content is being used.

### Eligible Content

- Daily note text
- User-created journal entries associated with the selected date
- Checklists and completed tasks
- Headings and sections
- User-added captions
- Optional transcribed voice notes
- Optional OCR text from images
- Optional linked notes explicitly selected by the user



### Excluded by Default

- Hidden files
- Deleted content
- Application logs
- System metadata
- Notes outside the selected date
- Sensitive sections marked as excluded
- Attachments without user permission
- Content from other applications



### User Controls

The user should be able to:

- Exclude specific sections
- Include linked notes
- Remove task-only sections
- Preview the processed source
- Cancel before sending content to an AI provider

---



## 16. AI Behavior Requirements

The reflection generator must:

1. Use only information present in the selected source content.
2. Separate direct facts from interpretations.
3. Avoid inventing events, emotions, relationships, or intentions.
4. Use tentative language when making interpretations.
5. Avoid overly positive, motivational, or therapeutic language unless requested.
6. Avoid repeating the source content line by line.
7. Prioritize meaningful patterns over minor details.
8. Acknowledge when the journal contains insufficient information.
9. Avoid diagnosing mental-health conditions.
10. Avoid presenting assumptions as established facts.
11. Avoid moral judgment.
12. Avoid commanding the user.
13. Keep the reflection personal but not intrusive.
14. Respect the user’s preferred tone and reflection depth.
15. Preserve the language used by the user unless another language is selected.



### Preferred Language Patterns

Use:

- “Your notes suggest…”
- “One possible theme is…”
- “It seems that…”
- “This may indicate…”
- “A question worth considering is…”

Avoid:

- “You are clearly…”
- “The real reason is…”
- “You suffer from…”
- “You should definitely…”
- “This proves that…”

---



## 17. Prompt Design

The prompt should define:

- The role of the reflection assistant
- The selected date
- The selected reflection mode
- The user’s source content
- The output structure
- Safety and grounding rules
- Language and length constraints
- Instructions for insufficient content



### Conceptual System Instruction

```text
You are a private journaling reflection assistant.

Create a thoughtful daily reflection based only on the journal content provided by the user.

Your purpose is to help the user understand what mattered, recognize progress and challenges, and identify a useful thought to carry forward.

Do not invent events, emotions, motivations, relationships, or conclusions.

When interpreting emotional or personal meaning, use tentative language such as “your notes suggest,” “it may be,” or “one possible theme.”

Do not diagnose the user, provide medical claims, or imitate a therapist.

Avoid generic motivational language. Keep the reflection grounded in concrete details from the journal.

When the source contains insufficient meaningful information, say so clearly and offer a small set of reflection questions instead.
```

---



## 18. Insufficient Content Experience

A reflection should not be generated as though meaningful information exists when the journal contains too little content.

### Insufficient Content Conditions

Examples:

- The note is empty.
- The note only contains a title or date.
- The note only contains one generic task.
- Most content is metadata or templates.
- The content is too short to support meaningful interpretation.



### Expected Experience

Display:

> There may not be enough journal content to create a meaningful reflection for this day.

Then offer prompts such as:

- What moment stood out today?
- What gave you energy?
- What drained your energy?
- What did you make progress on?
- What is still on your mind?
- What would you like to remember about today?

After the user answers one or more questions, the system may generate the reflection again.

---



## 19. Editing and Regeneration

After generation, the user can:

- Edit any part of the reflection
- Delete a section
- Regenerate the entire reflection
- Regenerate an individual section
- Make it shorter
- Make it deeper
- Make it more factual
- Make it more emotionally reflective
- Change the tone
- Restore the previous version before saving

For MVP, required actions are:

- Edit
- Regenerate
- Shorten
- Save
- Cancel

---



## 20. Feedback

The result screen should provide lightweight feedback controls.

### Feedback Options

- Helpful
- Not helpful

When the user selects **Not helpful**, optional reasons may include:

- Too generic
- Incorrect interpretation
- Missed important details
- Too long
- Too personal
- Repetitive
- Tone does not feel right
- Included content I did not expect

Feedback should not upload journal content unless the user has explicitly agreed to diagnostic data sharing.

---



## 21. Privacy and Security

Because journal content may be highly sensitive, privacy must be a core product requirement.

### Requirements

1. Journal content must not be sent to an external provider without clear disclosure.
2. The user must know which provider processes the reflection.
3. Content should be transmitted only over encrypted connections.
4. Raw journal content should not be stored on the application server unless required and explicitly disclosed.
5. Provider-side data retention should be minimized where supported.
6. API keys must be stored securely.
7. Reflections should remain private by default.
8. Analytics must not contain raw journal content.
9. Logs must not contain journal text, generated reflections, or API credentials.
10. Users must be able to delete saved reflections.
11. Users must be able to disable the feature.
12. Sensitive data should be redacted from crash reports.
13. Automatic generation must be opt-in.
14. The application should provide a local-processing option when technically available.



### Consent Message

Before first use:

> Daily Reflection uses the journal content you select to generate a private reflection. Depending on your configuration, this content may be processed by an external AI provider. Your journal will not be shared publicly, and the application will not use it for advertising.

The exact wording must reflect the real architecture and provider policy.

---



## 22. Safety Requirements

The feature may encounter journal entries involving distress, self-harm, abuse, medical issues, or crisis situations.

The model must not:

- Diagnose the user
- Provide unsupported medical advice
- Encourage dependency on the application
- Present itself as a therapist
- Minimize serious distress
- Generate guilt-inducing language
- Give overly confident interpretations

When the source indicates possible immediate danger, the reflection should shift away from normal contemplation and encourage the user to seek immediate help from local emergency services or a trusted person.

Safety handling must be carefully localized and reviewed before launch.

---



## 23. Functional Requirements



### FR-01: Date Selection

The user can generate a reflection for today or another selected date.

### FR-02: Source Retrieval

The application retrieves eligible journal content associated with the selected date.

### FR-03: Source Preview

The user can review the content before generation.

### FR-04: Source Exclusion

The user can exclude sections from processing.

### FR-05: Reflection Generation

The application generates a reflection using the selected content.

### FR-06: Loading State

The application displays generation progress and allows cancellation.

### FR-07: Result Rendering

The application renders the output as structured, readable sections.

### FR-08: Editing

The user can edit the generated reflection.

### FR-09: Regeneration

The user can regenerate the result.

### FR-10: Save Destination

The user can save the reflection to the configured destination.

### FR-11: Duplicate Protection

The application should detect an existing reflection for the selected date and ask whether to replace, append, or cancel.

### FR-12: Offline Handling

When remote generation is unavailable, the application should explain that an internet connection is required, unless an on-device model is configured.

### FR-13: Error Recovery

The user can retry after network, authentication, provider, or processing errors.

### FR-14: Language

The reflection should default to the language of the journal content.

### FR-15: User Preferences

The application stores the user’s preferred reflection mode, length, language, and save destination.

---



## 24. Non-Functional Requirements



### Performance

- Reflection screen should open within 500 milliseconds after local content is loaded.
- Source preview should be available without contacting the AI provider.
- Generation should support cancellation.
- Long notes should not freeze the UI.
- Content processing should run outside the main UI thread.



### Reliability

- Original journal content must never be overwritten unintentionally.
- Saving should use atomic or recoverable write operations.
- Failed generation must not create incomplete reflection sections.
- A generated result should remain recoverable if the user temporarily leaves the screen.



### Accessibility

- All actions must have screen-reader labels.
- Reflection headings should use semantic structure.
- Loading indicators must include accessible status text.
- The feature must support dynamic font sizes.
- Meaning must not depend only on color.
- Editing controls must support keyboard navigation where applicable.



### Localization

- UI strings must be localizable.
- Reflection language should follow the content or user preference.
- Date formats should follow locale preferences.
- Prompt templates must support multilingual generation.

---



## 25. Edge Cases

The product should handle:

- Empty journal
- Very short journal
- Extremely long journal
- Multiple languages in one note
- Journal containing only tasks
- Journal containing only completed tasks
- Journal containing template placeholders
- Duplicate reflection generation
- User edits the source while generation is running
- User closes the app during generation
- Network interruption
- Provider timeout
- Invalid or expired API key
- Provider rate limit
- Model refusal
- Malformed model output
- Markdown formatting errors
- Unsupported attachment
- Journal file becomes unavailable
- Storage permission is revoked
- Selected note is read-only
- Reflection heading already exists
- Journal contains highly sensitive content
- User requests deletion while processing is active

---



## 26. UX Requirements

The experience should feel calm and intentional rather than technical.

### UX Principles

- Show the source before sending it.
- Keep the main generation action obvious.
- Do not overload the user with AI configuration.
- Make interpretations visually distinguishable from direct facts where appropriate.
- Preserve user control at every stage.
- Avoid celebratory animations that feel inappropriate for serious journal content.
- Avoid streak pressure in the initial version.
- Make saving explicit.
- Never silently insert AI-generated content into the journal.



### Suggested Main Screen

**Header:** Reflect on Today
**Subtext:** Turn today’s journal into a thoughtful reflection.

Sections:

1. Source preview
2. Included content settings
3. Reflection style
4. Generate button
5. Privacy note

---



## 27. Analytics

Analytics must measure product behavior without collecting journal content.

### Core Events

- `daily_reflection_opened`
- `daily_reflection_source_previewed`
- `daily_reflection_generation_started`
- `daily_reflection_generation_completed`
- `daily_reflection_generation_failed`
- `daily_reflection_generation_cancelled`
- `daily_reflection_regenerated`
- `daily_reflection_edited`
- `daily_reflection_saved`
- `daily_reflection_deleted`
- `daily_reflection_helpful_selected`
- `daily_reflection_not_helpful_selected`
- `daily_reflection_insufficient_content`
- `daily_reflection_privacy_consent_accepted`
- `daily_reflection_privacy_consent_declined`



### Safe Event Properties

- Reflection mode
- Approximate source length bucket
- Generation duration
- Provider category
- On-device versus cloud processing
- Save destination
- Error category
- Selected language
- Whether the result was edited
- Whether the result was regenerated



### Prohibited Analytics Data

- Raw journal text
- Generated reflection text
- User names extracted from entries
- Detected emotions tied to the user
- Medical or mental-health information
- API credentials
- File paths containing private information

---



## 28. Success Metrics



### Activation

- Percentage of eligible users who open Daily Reflection
- Percentage of users who complete their first reflection
- Time from first journal entry to first reflection



### Engagement

- Reflections generated per active user per week
- Percentage of generated reflections that are saved
- Percentage of reflections edited before saving
- Reflection regeneration rate
- Reflection feature repeat-use rate



### Quality

- Helpful feedback rate
- Not-helpful rate
- Incorrect-interpretation feedback rate
- Too-generic feedback rate
- Save-after-generation rate



### Retention

- Seven-day retention among reflection users versus non-users
- Thirty-day retention among repeat reflection users
- Percentage of users generating reflections on three or more days per week



### Guardrail Metrics

- Generation failure rate
- Average generation latency
- User cancellation rate
- Privacy-consent decline rate
- Reflection deletion rate
- Safety escalation frequency
- Provider cost per completed reflection

---



## 29. MVP Acceptance Criteria

The MVP is ready when:

1. A user can generate a reflection from a daily note.
2. The user can preview the source content before generation.
3. The generated result follows the defined reflection structure.
4. The result is grounded in the source content.
5. The user can edit and regenerate the result.
6. The user can save the reflection into the daily note.
7. Existing journal content is not overwritten without confirmation.
8. Empty and insufficient-content states are handled.
9. Network and provider failures have recoverable error states.
10. Raw journal text is excluded from analytics and application logs.
11. The first-use privacy disclosure is implemented.
12. The user can disable Daily Reflection.
13. The feature supports at least the primary application language and English.
14. Accessibility labels and dynamic text sizing are supported.
15. Generation can be cancelled without corrupting user data.

---



## 30. Release Phases



### Phase 1: Manual Daily Reflection

- Manual generation
- Current daily note only
- Balanced reflection mode
- Source preview
- Edit, regenerate, save
- Privacy consent
- Basic feedback
- Cloud AI provider



### Phase 2: Personalization

- Reflection style selection
- Length control
- Custom reflection headings
- Multiple save destinations
- User-defined prompts
- Bring-your-own-key support
- Reflection history



### Phase 3: Proactive Reflection

- End-of-day reminders
- Automatic draft generation
- Reflection questions for insufficient content
- Voice-note and image-text inclusion
- On-device processing where available



### Phase 4: Long-Term Insight

- Weekly reflection
- Monthly reflection
- Recurring themes
- Goal and habit connections
- Search across reflections
- User-controlled long-term memory

---



## 31. Risks and Mitigations



### Risk: Generic Output

**Impact:** Reflection feels like a basic summary or motivational template.

**Mitigation:**

- Require references to concrete source details.
- Tune prompts using real anonymized test cases.
- Provide feedback reasons.
- Limit unnecessary generic advice.



### Risk: Hallucinated Interpretation

**Impact:** User loses trust because the system invents feelings or events.

**Mitigation:**

- Enforce grounded prompting.
- Use tentative language.
- Add automated grounding checks where feasible.
- Provide a “make it more factual” regeneration option.



### Risk: Privacy Concern

**Impact:** Users avoid the feature or distrust the application.

**Mitigation:**

- Source preview before processing.
- Clear provider disclosure.
- No raw-content analytics.
- Local generation roadmap.
- User-controlled deletion.



### Risk: Excessive Emotional Authority

**Impact:** The reflection sounds diagnostic or manipulative.

**Mitigation:**

- Explicit safety constraints.
- Avoid therapeutic framing.
- Review outputs involving emotional topics.
- Provide user feedback for “too personal.”



### Risk: High AI Cost

**Impact:** The feature becomes financially unsustainable.

**Mitigation:**

- Limit source length.
- Preprocess irrelevant content locally.
- Use efficient models.
- Cache unsaved results temporarily.
- Support bring-your-own-key.
- Use tiered limits.



### Risk: Long Journal Context

**Impact:** Important details are missed or model limits are exceeded.

**Mitigation:**

- Remove templates and metadata.
- Chunk and summarize sections.
- Rank content by relevance.
- Inform the user when only part of the note is included.

---



## 32. Open Product Decisions

The team must decide:

1. Should generation be cloud-based, on-device, or provider-configurable?
2. Should the feature use only the daily note or all entries created on that date?
3. Should task lists be included by default?
4. Should reflections be saved automatically as drafts or only after confirmation?
5. Should the application support bring-your-own-key in the first release?
6. What is the maximum source length?
7. Should linked notes be available in MVP?
8. Should reflection history exist outside the journal?
9. How should the system handle multiple languages?
10. Should the first version include safety-specific responses?
11. Should the generated reflection quote the user’s original wording?
12. Should edited reflections be distinguishable from fully AI-generated content?

---



## 33. Recommended MVP Decisions

To keep the first release controlled and useful:

- Generate manually rather than automatically.
- Use only the selected daily note.
- Include tasks but deprioritize unchecked boilerplate tasks.
- Require source preview before the first generation.
- Save only after explicit user confirmation.
- Use one Balanced Reflection template.
- Limit source content using a configurable character or token threshold.
- Exclude linked notes and attachments from MVP.
- Keep reflection history inside the journal rather than a separate database.
- Support the journal’s detected language.
- Provide edit, regenerate, shorten, and save actions.
- Add bring-your-own-key after the core experience is validated.

---



## 34. Example Empty State

**Title:** Reflect on your day

**Description:** Turn today’s journal into a thoughtful review of what mattered, what challenged you, and what you may want to carry into tomorrow.

**Primary action:** Generate Reflection

**Secondary action:** Review Included Content

**Privacy note:** Only the content you select will be used.

---



## 35. Product Principle

Daily Reflection should not tell the user who they are.

It should help the user see their own day more clearly.