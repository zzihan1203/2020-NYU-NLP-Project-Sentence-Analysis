# 2020-NYU-NLP-Project-Sentence-Analysis
2020 NYU NLP project - sentence analysis 

Zihan Zhang(zz2349), Shou-Hao Wu(shw344) 

We implement a sentiment analysis classifier model based on the review of NYU professors. The dataset comes from top reviews of NYU professors on www.ratemyprofessors.com, which is a website for students to evaluate their professors. We use Naive Bayes model with smoothing. We will use another model or method to improve the accuracy in the future. 

We train our model by parsing reviews from the website, assigning rating 1, 2, 3 as negative; 4, 5 as positive, weighted by number of people finding this review helpful/not helpful.

For testing we feed into a review entry to our program, and give a sentiment output indicating this review is a positive or negative one.

# Results
- Simple bag of words
  - 1127 out of 1774 sentences is correctly classified.
  - accuracy:    63.53
- Bag of words with stop-words filtered (removing common words from train/dev corpus)
  - 1252 out of 1770 sentences is correctly classified.
  - accuracy:    70.73
- Bag of words with Boolean Naive Bayes (removing duplicate words in each review before training)
  - 1132 out of 1774 sentences is correctly classified.
  - accuracy:    63.81
- Bag of words with stop-words filtered and Boolean Naive Bayes
  - 1255 out of 1770 sentences is correctly classified.
  - accuracy:    70.90
