# 2020-NYU-NLP-Project-Sentence-Analysis
2020 NYU NLP project - sentence analysis 

Zihan Zhang(zz2349), Shou-Hao Wu(shw344) 

We implement a sentiment analysis classifier model based on the review of NYU professors. The dataset comes from top reviews of NYU professors on www.ratemyprofessors.com, which is a website for students to evaluate their professors. We use Naive Bayes model with smoothing. We will use another model or method to improve the accuracy in the future. 

We train our model by parsing reviews from the website, assigning rating 1, 2, 3 as negative; 4, 5 as positive, weighted by number of people finding this review helpful/not helpful.

For testing we feed into a review entry to our program, and give a sentiment output indicating this review is a positive or negative one.

# Running the code
- Data retrieval and bag-of-words generation
  - Make sure URLs is listed in DataSet/doc/NYU.txt and stop-words are listed in Niave_Bayes/data/stopwords.txt
  - Simply run DataSet/src/Driver.java then the training process shall begin
- Model training and testing
  - Run Naive_Bayes/src/Bayes.java with two arguments: the path to the training dataset, the path to the testing dataset.
  - Remember to change the output file name and path accordingly on line 28.
- Model Evaluation
  - Run Naive_Bayes/src.Score.java with two arguments: the path to the result file, the path to the standard key file.
    The relationships between files under Naive_Bayes/data are: 
    training dataset -> testing dataset -> result dataset -> key dataset:
    bag.txt -> testWords.txt -> results.txt -> standard.txt
    bag-filtered.txt -> testWords-filtered.txt -> results-filtered.txt -> standard-filtered.txt
    bag-boolean.txt -> testWords-boolean.txt -> results-boolean.txt -> standard-boolean.txt
    bag-filtered-boolean.txt -> testWords-filtered-boolean.txt -> results-filtered-boolean.txt -> standard-filtered-boolean.txt
    bag-filtered-boolean-weighted.txt -> testWords-filtered-boolean-weighted.txt -> results-filtered-boolean-weighted.txt -> standard-filtered-boolean-weighted.txt
   
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
- Bag of words with stop-words filtered, Boolean Naive Bayes, and word frequency weighted by number of votes that consider this review helpful
  - 1297 out of 1777 sentences is correctly classified.
  - accuracy:    72.99
