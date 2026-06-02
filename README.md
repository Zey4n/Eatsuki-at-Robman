# Eatsuki: RobMan 🍱✨
> "Break the comfort food loop. Complete the local culinary map."

**Eatsuki: RobMan** is a utility-driven, gamified mobile application designed specifically for the community at the University of the Philippines Manila. It solves a ubiquitous student dilemma: the severe decision fatigue associated with choosing where to eat during class breaks at Robinsons Place Manila.

While the mall hosts a massive directory of dining options, students frequently experience a "comfort food loop," repeatedly visiting the same few familiar establishments throughout their stay. Eatsuki breaks this loop by cataloging every single dining and dessert option in the mall and transforming the selection process into a high-engagement, **Gacha-style random draw**.

---

## 🎯 Project Objectives
The ultimate goal of the application is **absolute exploration**. Over a four-year academic lifecycle, the app systematically tracks, incentivizes, and guides users to successfully dine at 100% of the food establishments within Robinsons Place Manila without repeating past options.

---

## 🛠️ Core Mechanical Architecture

### 1. The Gacha Roll Flow
* **Category Filter:** Users initiate a binary choice to isolate their current craving: **Dine** (heavy full meals) or **Desserts** (milk tea, coffee, snacks).
* **Randomizer Draw Engine:** Executes an algorithm pulling exclusively from the active filtered subset of establishments.
* **Metadata Display:** Returns the drawn shop alongside critical local attributes: Price Range tier, Floor/Wing Level, Community Ratings, and a curated Culinary Description.

### 2. The Pool Exclusion Engine (Core Mechanism)
To make the "completionist" goal mathematically viable, the repository utilizes a dynamic database status engine:
* Once an establishment is officially **Accepted** and **Confirmed**, it is instantly flagged as `Visited` in the database.
* Subsequent rolls completely omit all flagged entities, forcing the algorithm to distribute chances strictly among unvisited options.

### 3. Overview Hub & Statistical Analytics
* **Completion Analytics:** Tracks real-time completion progression metrics (e.g., *"23 out of 87 Shops Visited"*).
* **Dynamic Rating Journal:** Gives users full authority to log, input, and retroactively edit personal ratings, creating an implicit feedback loop for future manual returns.

---

## 📱 UI/UX Design Language
Built with an emphasis on mitigating decision paralysis, the interface features:
* **Structural Flatness:** No deep menus or multi-layered navigation hierarchies; immediate conduit from launch to roll within seconds.
* **High-Contrast Typography:** Clear emphasis on the drawn establishment name paired with understated, clean animation transitions.
