import baker


from nyt import generate_nyt_dataset

@baker.command
def nyt_salience_dataset():
    generate_nyt_dataset()