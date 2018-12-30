package recommender;

/**
 * UserSimilarityScore stores a userID, similarityscore pair
 * This was made to be able to sort userIDs by similarityscore
 */
public class UserSimilarityScore implements Comparable<UserSimilarityScore>{
    private double score;
    private int id;

    /**
     * Constructor
     * @param id user id
     * @param score Pearson Coefficient
     */
    public UserSimilarityScore(int id, double score)
    {
        this.id = id;
        this.score = score;
    }

    /**
     * returns similarity score
     * @return score
     */
    public double getScore() {
        return score;
    }

    /**
     * returns user ID
     * @return id
     */
    public int getId() {
        return id;
    }

    /**
     * Sorts the UserSimilarityScores in descending order
     * @param user UserSimilarityScore being compared
     * @return
     */
    @Override
    public int compareTo(UserSimilarityScore user) {
        //I implemented threshold elsewhere. I feel like it isn't as effective here
        if(Math.abs(this.score - user.getScore()) == 0)
        {
            return 0;
        }
        else if(this.score < user.getScore())
        {
            return 1;
        }
        return -1;
    }
}
